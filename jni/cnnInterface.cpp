/*
Copyright (c) 2016, Taiga Nomi
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
* Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
* Neither the name of the <organization> nor the
names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
#include "cnnInterface.h"

cv::Mat compute_mean(const string& mean_file, int width, int height)
{
    caffe::BlobProto blob;
    detail::read_proto_from_binary(mean_file, &blob);
    vector<cv::Mat> channels;
    auto data = blob.mutable_data()->mutable_data();

    for (int i = 0; i < blob.channels(); i++, data += blob.height() * blob.width())
        channels.emplace_back(blob.height(), blob.width(), CV_32FC1, data);
    cv::Mat mean;
    cv::merge(channels, mean);
    return cv::Mat(cv::Size(width, height), mean.type(), cv::mean(mean));
}

cv::ColorConversionCodes get_cvt_codes(int src_channels, int dst_channels)
{
    assert(src_channels != dst_channels);

    if (dst_channels == 3)
        return src_channels == 1 ? cv::COLOR_GRAY2BGR : cv::COLOR_BGRA2BGR;
    else if (dst_channels == 1)
        return src_channels == 3 ? cv::COLOR_BGR2GRAY : cv::COLOR_BGRA2GRAY;
    else
        throw runtime_error("unsupported color code");
}

template <typename Activation>
double rescale(double x) {
	Activation a;
	return 100.0 * (x - a.scale().first) / (a.scale().second - a.scale().first);
}


void convert_image(cv::Mat * img ,
	double scale,
	int w,
	int h,
	vec_t& data) {

	//auto img = cv::imread(imagefilename, cv::IMREAD_GRAYSCALE);
	if (img->data == nullptr) return; // cannot open, or it's not an image

	cv::Mat_<uint8_t> resized ;
	cv::resize(*img , resized , cv::Size(w, h));

	// mnist dataset is "white on black", so negate required
	std::transform(resized.begin(), resized.end(), std::back_inserter(data),
			[=](uint8_t c) { return c * scale;});
}


void preprocess(const cv::Mat& img,
				const cv::Mat& mean,
                int num_channels,
                cv::Size geometry,
                vector<cv::Mat>* input_channels)
{
    cv::Mat sample;
    // convert color
    if (img.channels() != num_channels)
        cv::cvtColor(img , sample, get_cvt_codes(img.channels(), num_channels));
    else
        sample = img;
    // resize
    cv::Mat sample_resized;
    cv::resize(sample , sample_resized , geometry);

    cv::Mat sample_float;
    sample_resized.convertTo(sample_float, num_channels == 3 ? CV_32FC3 : CV_32FC1);

    // subtract mean
    if (mean.size().width > 0) {
        cv::Mat sample_normalized;
        cv::subtract(sample_float, mean , sample_normalized);
        cv::split(sample_normalized, *input_channels);
    }
    else {
        cv::split(sample_float, *input_channels);
    }
}


void construct_net(network<mse, gradient_descent_levenberg_marquardt>& nn) {
	// connection table [Y.Lecun, 1998 Table.1]
#define O true
#define X false
	static const bool tbl[] = {
			O, X, O, O, O, O, O, O, O, O, O, X, O, O, O, O,
			O, X, X, X, O, O, O, X, X, O, O, O, X, X, O, O,
			O, O, X, X, X, O, O, O, X, X, O, O, X, X, X, O,
			O, O, O, X, X, X, O, O, O, X, X, O, X, X, O, O,
			O, O, O, O, X, X, O, O, O, O, X, X, O, X, X, O,
			O, X, O, O, O, X, X, O, O, O, O, X, O, O, X, O,
	};
#undef O
#undef X

	// construct nets
	nn << convolutional_layer<tan_h>(40 , 40 , 3 , 1 , 6)
		<< average_pooling_layer<tan_h>(38 , 38 , 6 , 2)
		<< convolutional_layer<tan_h>(19 , 19 , 4 , 6 , 16 ,
		connection_table(tbl, 6, 16))
		<< average_pooling_layer<tan_h>(16 , 16 , 16 , 2)
		<< convolutional_layer<tan_h>(8 , 8 , 3 , 16, 16)
		<< fully_connected_layer<tan_h>(16 * 6 * 6 , 64)
		<< fully_connected_layer<relu>(64 , 2);
}

 void createPredictor(
	const string& model_file,
	const string& trained_file ,
	PredictorHandle * handle
	)
{
    static auto net = create_net_from_caffe_prototxt(model_file);
	cout << typeid(net).name() << endl;
	reload_weight_from_caffe_protobinary(trained_file ,  net.get());
	*handle = &net;
}

 void createPredictor(
	const string& trained_file ,
	PredictorHandle& handle
	)
{
	 static network<mse, gradient_descent_levenberg_marquardt> nn ;
	 construct_net(nn);
	 // load nets
	 ifstream ifs(trained_file.c_str());
	 ifs >> nn;
	 handle = &nn;
}

int getResultforCaffe(long h ,
		  string  mean_file ,
          cv::Mat * img_file)
{
   // auto labels = get_label_list(label_file);
	PredictorHandle handle = (PredictorHandle)h;
	auto p = static_cast<std::shared_ptr<tiny_cnn::network<tiny_cnn::mse , tiny_cnn::adagrad>>*>(handle);
	//tiny_cnn::network<tiny_cnn::mse , tiny_cnn::adagrad>* p = (tiny_cnn::network<tiny_cnn::mse , tiny_cnn::adagrad>*)(handle);
	debug("get3");
    int channels = (*p)->in_shape().depth_;
    int height = (*p)->in_shape().height_;
    int width = (*p)->in_shape().width_;
    debug("get4");
	//cout << channels << " " << width << " " << height;
    auto mean = compute_mean(mean_file , width, height);

    vector<float> inputvec(width*height*channels);
    vector<cv::Mat> input_channels;
    debug("get5");
    for (int i = 0; i < channels; i++)
		/*c++11µÄÌØÐÔ*/
        input_channels.emplace_back(height , width , CV_32FC1 , &inputvec[width*height*i]);

    preprocess(*img_file , mean , 3 , cv::Size(width, height), &input_channels);
    debug("get6");
    vector<tiny_cnn::float_t> vec(inputvec.begin(), inputvec.end());

	auto result = (*p)->predict(vec);

    vector<tiny_cnn::float_t> sorted(result.begin(), result.end());
    int top_n = 2;
    partial_sort(sorted.begin(), sorted.begin()+top_n, sorted.end(), greater<tiny_cnn::float_t>());

    size_t idx = distance(result.begin(), find(result.begin(), result.end(), sorted[0]));

	return idx;
}

int getResult(long h ,
          cv::Mat * img)
{
	debug("getResult1");
	PredictorHandle handle = (PredictorHandle)h ;
	auto nn = (network<mse , gradient_descent_levenberg_marquardt> *)(handle);
	debug("getResult2");
	vec_t data ;
	convert_image(img , 1.0 , 40 , 40 , data);
	debug("getResult3");
	// recognize
	auto res = nn->predict(data);

	debug("getResult4");
	vector<pair<double, int> > scores;
	// sort & print top-3
	for (int i = 0; i < 2; i++)
		scores.emplace_back(rescale<tan_h>(res[i]), i);
	sort(scores.begin(), scores.end(), greater<pair<double, int>>());
	for (int i = 0; i < 2; i++)
	{
		debug("result = %d , %f", scores[i].second , scores[i].first);
	}
	// visualize outputs of each layer
	//for (size_t i = 0; i < nn.depth(); i++) {
	//	auto out_img = nn[i]->output_to_image();
	//	cv::imshow("layer:" + std::to_string(i), image2mat(out_img));
	//}
	// visualize filter shape of first convolutional layer
	//auto weight = nn.at<convolutional_layer<tan_h>>(0).weight_to_image();
	//cv::imshow("weights:", image2mat(weight));

	return scores[0].second ;
}



int main(int argc, char** argv) {

//    int arg_channel = 1 ;
//	string model_file = "C:\\opencv\\face_models\\deploy_V2.prototxt";
//    string trained_file = "C:\\opencv\\face_models\\_iter_100000.caffemodel";
//    //string mean_file = argv[arg_channel++];
//    //string label_file = argv[arg_channel++];
//    string img_file = "C:\\opencv\\face_models\\image2.png";
//
//    try {
//
//        test(model_file, trained_file , img_file);
//
//    } catch (const nn_error& e) {
//        cout << e.what() << endl;
//    }
//
//	system("pause");

}

//JNIEXPORT jint JNICALL Java_jni_testCnn_jniPredict2(JNIEnv *env , jclass obj , jlong addr , jstring model , jstring proto)
//{
//	jint result ;
//	///jintArray  inner = env->NewIntArray(2);
//	/*save the result of predicting*/
//	//jint *pArray ;
//	//pArray = (jint *)calloc(2 , sizeof(jint));
//	const char *cmd = env->GetStringUTFChars(model , 0);
//	const char *p = env->GetStringUTFChars(proto , 0);
//	debug("jniPredict cmd = %s", cmd);
//	debug("proto cmd = %s" , p);
//
//	//std::vector<char*> v;
//	cv::Mat * src = (cv::Mat *)addr ;
//	result = test(p , cmd , src);
//	// add dummy head to meet argv/command format
//	//std::string cmdString = std::string(cmd);
//	//env->SetIntArrayRegion(inner , 0 , 2 , pArray);
//	//free(pArray);
//	// free java object memory
//	env->ReleaseStringUTFChars(model , cmd);
//	env->ReleaseStringUTFChars(proto , p);
//	return result;
//}

/**/

JNIEXPORT jlong JNICALL Java_jni_Predictor_createPredictorforCaffe(JNIEnv *env , jclass obj , jstring symbol , jstring params)
{
	const char *s = env->GetStringUTFChars(symbol , 0);
	const char *p = env->GetStringUTFChars(params , 0);
	debug("jniPredict cmd = %s", s);
	debug("proto cmd = %s" , p);

	PredictorHandle pre = 0 ;

	createPredictor(s , p , &pre);

	env->ReleaseStringUTFChars(symbol , s);
	env->ReleaseStringUTFChars(params , p);

	return (jlong)pre;
}

JNIEXPORT jlong JNICALL Java_jni_Predictor_createPredictor(JNIEnv *env , jclass obj , jstring params)
{
	const char *s = env->GetStringUTFChars(params , 0);
	debug("jniPredict cmd = %s", s);

	PredictorHandle pre = 0 ;

	createPredictor(s, pre);

	env->ReleaseStringUTFChars(params , s);

	return (jlong)pre;
}

/**/

JNIEXPORT jint JNICALL Java_jni_Predictor_getPredict(JNIEnv *env , jclass obj , jlong handle , jlong addr , jint type , jstring caffeMean)
{
	const char *mean = env->GetStringUTFChars(caffeMean , 0);

	jint result ;
	cv::Mat *src = (cv::Mat *)addr;
	debug("get1");
	if(type == 0)
	result = getResultforCaffe(handle , mean , src);
	else
	result = getResult(handle , src);

	debug("get2");

	env->ReleaseStringUTFChars(caffeMean , mean);

	return result;
}
