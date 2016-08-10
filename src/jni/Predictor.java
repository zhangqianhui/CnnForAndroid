package jni;


public class Predictor{
	
	/*the type of model include caffe or tinycnn*/
	
	public static class ModelType {
		
		 Type modeltype ;
		
		 public enum Type {
			 
		      TinyCNN , CAFFE
		 }
		 
		 public ModelType(Type type)
		 {
			 modeltype = type ;
		 }
		 
		 int ctype()
		 {
			 /**/
			 return  this.modeltype == Type.TinyCNN ? 1 : 0; 
		 }
	}
	
   private long handle = 0 ;
   
   public long getHandle()
   {
	   return handle ;
   }
   
   public  Predictor(ModelType type) {
	  
	   if(type.ctype() == 0)
	   this.handle = createPredictorforCaffe(testCnn.caffePro , testCnn.caffeModel);
	   else
	   {
		   this.handle = createPredictor(testCnn.tiny_file);
	   }
   }
		
   /*jni func 
    *   create the predictor
    * */
   public  static native long createPredictor(String symbol);
   
   public  static native long createPredictorforCaffe(String symbol , String params);
   
   /*jni func
    * get the feature array
    * */
   public static native float[] getFeacture(long handle , long Mataddr);
   
   /*jni func
    * get the predict's result
    * */
   
   public static native int getPredict(long handle , long Mataddr , int type);

   
}
