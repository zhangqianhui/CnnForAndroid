#include "test.h"

// rescale output to 0-100
template <typename Activation>
double rescale(double x) {
	Activation a;
	return 100.0 * (x - a.scale().first) / (a.scale().second - a.scale().first);
}

// convert tiny_cnn::image to cv::Mat and resize
cv::Mat image2mat(image<>& img) {

	cv::Mat ori(img.height(), img.width(), CV_8U, &img.at(0, 0));
	cv::Mat resized;
	cv::resize(ori, resized, cv::Size(), 3 , 3 , cv::INTER_AREA);
	return resized;
}
void convert_image(Mat * img ,
	double scale,
	int w,
	int h,
	vec_t& data) {

	//auto img = cv::imread(imagefilename, cv::IMREAD_GRAYSCALE);
	if (img->data == nullptr) return; // cannot open, or it's not an image

	cv::Mat_<uint8_t> resized;
	cv::resize(*img, resized , cv::Size(w, h));

	// mnist dataset is "white on black", so negate required
	std::transform(resized.begin(), resized.end(), std::back_inserter(data),
			[=](uint8_t c) { return c * scale;});
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
	nn << convolutional_layer<tan_h>(40 , 40 , 3 , 1 , 6)  // C1, 1@32x32-in, 6@28x28-out
		<< average_pooling_layer<tan_h>(38 , 38 , 6 , 2)   // S2, 6@28x28-in, 6@14x14-out
		<< convolutional_layer<tan_h>(19 , 19 , 4 , 6 , 16 ,
		connection_table(tbl, 6, 16))              // C3, 6@14x14-in, 16@10x10-in
		<< average_pooling_layer<tan_h>(16 , 16 , 16 , 2)  // S4, 16@10x10-in, 16@5x5-out
		<< convolutional_layer<tan_h>(8 , 8 , 3 , 16, 16) // C5, 16@5x5-in, 120@1x1-out
		<< fully_connected_layer<tan_h>(16 * 6 * 6 , 64)
		<< fully_connected_layer<relu>(64 , 2);       // F6, 120-in, 10-out
}

/*the result of predicting*/
int recognize(const std::string& dictionary , Mat * img) {

	network<mse, gradient_descent_levenberg_marquardt> nn;
	construct_net(nn);
	// load nets
	ifstream ifs(dictionary.c_str());
	ifs >> nn;
	// convert imagefile to vec_t
	vec_t data;
	convert_image(img , 1.0 , 40 , 40 , data);
	// recognize
	auto res = nn.predict(data);
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

JNIEXPORT jint JNICALL Java_jni_testCnn_jniPredict(JNIEnv *env , jclass obj , jlong addr , jstring cmdIn){

	jint result ;
	///jintArray  inner = env->NewIntArray(2);
	/*save the result of predicting*/
	//jint *pArray ;
	//pArray = (jint *)calloc(2 , sizeof(jint));
	const char *cmd = env->GetStringUTFChars(cmdIn , 0);
	debug("jniPredict cmd = %s", cmd);
	//std::vector<char*> v;
	Mat * src = (Mat *)addr ;

	result = recognize(cmd , src) ;
	// add dummy head to meet argv/command format
	//std::string cmdString = std::string(cmd);

	//env->SetIntArrayRegion(inner , 0 , 2 , pArray);

	//free(pArray);
	// free java object memory
	env->ReleaseStringUTFChars(cmdIn , cmd);
	return result;
}


//int main(int argc , char** argv)
//{
//	//if (argc != 2) {
//	//	cout << "please specify image file";
//	//	return 0;
//	//}
//	char * path = "C:\\opencv\\carlogo\\1.jpg";
//	recognize("E:\\tiny-cnn-master\\vc\\vc12\\carlogo-weights", path);
//}
