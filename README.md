##CnnForAndroid:A Vehicle Recognition Project using Convolutional Neural Network(CNN) in Android platform。

CnnForAndroid is a android platform's implementation of deep learning using Tiny-cnn structure and provide a Vehicle Recognition sample.

#Dependencies

[Opencv](http://opencv.org/)(for Android platform)

[Tiny-cnn](https://github.com/nyanp/tiny-cnn#features)

#For Vehicle Recogniton 

1.What is Vehicle Recognition?

 this project classify Car according to Car logo.Now  the lasting Version just distinguish VM car from other version.
 In the future I will add more category。

2.Where from Training Data ?

The major sources of our dataset include images captured by ourselves,Medialab LPR Dataset [1].

![](https://github.com/zhangqianhui/CnnForAndroid/blob/master/photo%20for%20readme/20.jpg)
![](https://github.com/zhangqianhui/CnnForAndroid/blob/master/photo%20for%20readme/21.jpg) 

3.this models?

You can get it in JNi/test.cpp file.

Code:
```cpp

  static const bool tbl[] = {
			O, X, O, O, O, O, O, O, O, O, O, X, O, O, O, O,
			O, X, X, X, O, O, O, X, X, O, O, O, X, X, O, O,
			O, O, X, X, X, O, O, O, X, X, O, O, X, X, X, O,
			O, O, O, X, X, X, O, O, O, X, X, O, X, X, O, O,
			O, O, O, O, X, X, O, O, O, O, X, X, O, X, X, O,
			O, X, O, O, O, X, X, O, O, O, O, X, O, O, X, O,
	};

	 nn << convolutional_layer<tan_h>(40 , 40 , 3 , 1 , 6)  
		<< average_pooling_layer<tan_h>(38 , 38 , 6 , 2)   
		<< convolutional_layer<tan_h>(19 , 19 , 4 , 6 , 16 ,
		connection_table(tbl, 6, 16))              
		<< average_pooling_layer<tan_h>(16 , 16 , 16 , 2)  
		<< convolutional_layer<tan_h>(8 , 8 , 3 , 16, 16) 
		<< fully_connected_layer<tan_h>(16 * 6 * 6 , 64)
		<< fully_connected_layer<relu>(64 , 2);
		
```
 My models have three conv-layers and two pooling layer , fully-connect layer , in the end layer , the activation functions is relu and the size of all conv-kernel is 3x3.The optimization algorithm of cnn  is stochastic gradient levenberg marquardt.
 
 Other arguments can't be public.
 
#How to use it?
 
 Just download or git clone it and then open it by eclipse with opencv for java lib ,  use NDK to build it.
 Surely your libs docu have the opencv_java.so file or you must add this file(from Opencv-android docu).
 
#How to use it to recognize face or other object?

If want to recognition other object , you must learning Cnn and tiny-cnn , contructing optimal model and training it using 
enough object images to get the wb-file(weights and bias values).Finish , replace /assets/tinyfile/carlogo file with wb-file.

#references about Vehicle Recogniton

[1]Medialab LPR dataset, March. 2013[online]. Available: http://www.medialab.ntua.gr/research/LPRdataset.html

[2]Humayun Karim Sulehria, Ye Zhang.Vehicle Logo Recognition Using Mathematical Morphology

[3]Humayun Karim Sulehria, Ye Zhang.Vehicle Logo Recognition Based on Bag-of-Words.IEEE International Conference on Advanced Video and Signal Based Surveillance,2013.

#Running Screenshot

![](https://github.com/zhangqianhui/CnnForAndroid/blob/master/photo%20for%20readme/Screenshot_2016-05-30-17-36-03.png.jpg)
![](https://github.com/zhangqianhui/CnnForAndroid/blob/master/photo%20for%20readme/Screenshot_2016-05-30-17-36-08.png.jpg) 

#Discussing
* [issue](https://github.com/zhangqianhui/CnnForAndroid/issues/new)
* email:zhang163220@gmail.com
