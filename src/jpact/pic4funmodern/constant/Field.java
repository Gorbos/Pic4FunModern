package jpact.pic4funmodern.constant;

public class Field {
	
	//Crop
	public static final int SET_AS = 1;
	public static final int CROP = 2;
	public static final int ROT_LEFT = 3;
	public static final int ROT_RIGHT = 4;
	
	//Basic Requests
	public static final int CAMERA_REQUEST = 100;
	public static final int VIDEO_REQUEST = 200;
	public static final int GALLERY_REQUEST = 300;
	public static final int CAMERA_CROP_REQUEST = 500;
	public static final int NON_CAMERA_CROP_REQUEST = 200;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	

	//Tint
  	public static final double PI = 3.14159d;
  	public static final double FULL_CIRCLE_DEGREE = 360d;
  	public static final double HALF_CIRCLE_DEGREE = 180d;
  	public static final double RANGE = 256d;
  	//Sharpen from blog
  	final static int KERNAL_WIDTH = 3;
  	final static int KERNAL_HEIGHT = 3;
  	int[][] kernalBlur = {
  	   {0, -1, 0},
  	   {-1, 5, -1},
  	   {0, -1, 0}
  	};
  	
  	//id for dynamic resizing
  	public static final int ORIGINAL = 1;
  	public static final int MEDIUM = 2;
  	public static final int LARGE = 3;
  	public static final int DYNAMIC = 4;
	public static final int RETURNIMAGE = 5;
	
	//id for dynamic BGs in collage dialog
	public static final int BDAY1 = 1;
	public static final int BDAY2 = 2;
	public static final int BDAY3 = 3;
	public static final int BDAY4 = 4;
	public static final int BDAY5 = 5;
	public static final int ANNIVERSARY1 = 6;
	public static final int ANNIVERSARY2 = 7;
	public static final int ANNIVERSARY3 = 8;
	public static final int LOVE1 = 9;
	public static final int LOVE2 = 10;
	public static final int LOVE3 = 11;
	public static final int HALLOWEEN1 = 12;
	public static final int HALLOWEEN2 = 13;
	public static final int CHRISTMAS1 = 14;
	public static final int LEAVES1 = 15;
	public static final int LEAVES2 = 16;
	public static final int LEAVES3 = 17;
	public static final int LEAVES4 = 18;
	public static final int ARROWS1 = 19;
	public static final int FLEURDELIS = 20;
	public static final int CAMO1 = 21;
	public static final int SHAPES1 = 22;
	public static final int SHAPES2 = 23;
	public static final int CIRCLES1 = 24;
	public static final int CIRCLES2 = 25;
	public static final int ABSTRACT1 = 26;
	public static final int ABSTRACT2 = 27;
	public static final int ABSTRACT3 = 28;
	
	public static final int FIRST_IMAGE = 100001;
	public static final int SECOND_IMAGE = 100002;
	public static final int THIRD_IMAGE = 100003;
	public static final int FOURTH_IMAGE = 100004;
	public static final int FIFTH_IMAGE = 100005;
	public static final int SIXTH_IMAGE = 100006;
	public static final int SEVENTH_IMAGE = 100007;
	public static final int EIGHT_IMAGE = 100008;
	public static final int NINETH_IMAGE = 100009;
	public static final int TENTH_IMAGE = 100010;
	
	//for 15 image build
	public static final int ELEVENTH_IMAGE = 100011;
	public static final int TWELVETH_IMAGE = 100012;
	public static final int THIRTEENTH_IMAGE = 100013;
	public static final int FOURTEENTH_IMAGE = 100014;
	public static final int FIFTEENTH_IMAGE = 100015;
  	
  	//Toast showtime
  	public static final int SHOWTIME = 5000;
  	
  	//Folder name
//  	public static final String APPNA
}
