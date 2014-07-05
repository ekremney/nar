package com.beardedhen.androidbootstrap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.FontAwesomeText.AnimationSpeed;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 

public class BootstrapButton extends FrameLayout {

	private static Map<String, BootstrapTypes> bbuttonTypeMap;
	private static Map<String, BootstrapTypes> bbuttonTypeMapRounded;
	private static Typeface font;
	
	private static Map<String, String> faMap;
	
	private TextView lblMiddle;
	private TextView lblRight;
	private TextView lblLeft;
	private ViewGroup layout;
	private boolean roundedCorners = false;
	private boolean fillparent = false;
	
	private static final String FA_ICON_QUESTION = "fa-question";
	
	public static final int BB_ICON_LEFT = 0x1, BB_ICON_RIGHT = 0x2, BB_ICON_BOTH = 0x3;
	
	static{
		
		bbuttonTypeMap = new HashMap<String, BootstrapTypes>();
		
		bbuttonTypeMap.put("default", BootstrapTypes.DEFAULT);
		bbuttonTypeMap.put("primary", BootstrapTypes.PRIMARY);
		bbuttonTypeMap.put("success", BootstrapTypes.SUCCESS);
		bbuttonTypeMap.put("info", BootstrapTypes.INFO);
		bbuttonTypeMap.put("warning", BootstrapTypes.WARNING);
		bbuttonTypeMap.put("danger", BootstrapTypes.DANGER);
		bbuttonTypeMap.put("inverse", BootstrapTypes.INVERSE);
		
		bbuttonTypeMapRounded = new HashMap<String, BootstrapTypes>();
		
		bbuttonTypeMapRounded.put("default", BootstrapTypes.DEFAULT_ROUNDED);
		bbuttonTypeMapRounded.put("primary", BootstrapTypes.PRIMARY_ROUNDED);
		bbuttonTypeMapRounded.put("success", BootstrapTypes.SUCCESS_ROUNDED);
		bbuttonTypeMapRounded.put("info", BootstrapTypes.INFO_ROUNDED);
		bbuttonTypeMapRounded.put("warning", BootstrapTypes.WARNING_ROUNDED);
		bbuttonTypeMapRounded.put("danger", BootstrapTypes.DANGER_ROUNDED);
		bbuttonTypeMapRounded.put("inverse", BootstrapTypes.INVERSE_ROUNDED);
		
		
		faMap = FontAwesome.getFaMap();
		
	}
	
	public BootstrapButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialise(attrs);
	}

	public BootstrapButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(attrs);
	}

	public BootstrapButton(Context context) {
		super(context);
		initialise(null);
	}

	//set up the bootstrap types
	private enum BootstrapTypes
	{
		DEFAULT(R.drawable.bbuton_default, R.color.black),
		PRIMARY(R.drawable.bbuton_primary, R.color.white),
		SUCCESS(R.drawable.bbuton_success, R.color.white),
		INFO(R.drawable.bbuton_info, R.color.white),
		WARNING(R.drawable.bbuton_warning, R.color.white),
		DANGER(R.drawable.bbuton_danger, R.color.white),
		INVERSE(R.drawable.bbuton_inverse, R.color.white),
		
		DEFAULT_ROUNDED(R.drawable.bbuton_default_rounded, R.color.black),
		PRIMARY_ROUNDED(R.drawable.bbuton_primary_rounded, R.color.white),
		SUCCESS_ROUNDED(R.drawable.bbuton_success_rounded, R.color.white),
		INFO_ROUNDED(R.drawable.bbuton_info_rounded, R.color.white),
		WARNING_ROUNDED(R.drawable.bbuton_warning_rounded, R.color.white),
		DANGER_ROUNDED(R.drawable.bbuton_danger_rounded, R.color.white),
		INVERSE_ROUNDED(R.drawable.bbuton_inverse_rounded, R.color.white);

		private int backgroundDrawable;
		private int textColour;
		
		BootstrapTypes(int backgroundDrawable, int textColour)
		{
			this.backgroundDrawable = backgroundDrawable;
			this.textColour = textColour;
		}
	}
	
	
	private void initialise( AttributeSet attrs )
	{
		LayoutInflater inflator = (LayoutInflater)getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

		//get font
		readFont(getContext());

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.BootstrapButton);
		
		//defaults
		BootstrapTypes type;
		String bootstrapType = "default";
		String iconLeft = "";
		String iconRight = "";
		String text = "";
		//boolean roundedCorners = false;
		float fontSize = 14.0f;
		float scale = getResources().getDisplayMetrics().density; //for padding
		String size = "default";
		int paddingA = (int) (10 *scale + 0.5f);
		int paddingB = (int) (15 *scale + 0.5f);
		

		//attribute values
		
		if (a.getString(R.styleable.BootstrapButton_bb_type) != null) {
			bootstrapType = a.getString(R.styleable.BootstrapButton_bb_type);
		}
		
		if (a.getString(R.styleable.BootstrapButton_bb_roundedCorners) != null) {
			roundedCorners = a.getBoolean(R.styleable.BootstrapButton_bb_roundedCorners, false) ;
		}
		
		if(a.getString(R.styleable.BootstrapButton_bb_size) != null) {
			size = a.getString(R.styleable.BootstrapButton_bb_size);
		}
		
		if ( a.getString(R.styleable.BootstrapButton_bb_icon_left) != null) {
			iconLeft =  a.getString(R.styleable.BootstrapButton_bb_icon_left );
		}
		
		if(a.getString(R.styleable.BootstrapButton_bb_icon_right) != null) {
			iconRight = a.getString(R.styleable.BootstrapButton_bb_icon_right );
		}
		
		if(a.getString(R.styleable.BootstrapButton_android_text) != null) {
			text = a.getString(R.styleable.BootstrapButton_android_text);
		}
		String gravity = "";
		if(a.getString(R.styleable.BootstrapButton_bb_text_gravity) != null) {
			gravity = a.getString(R.styleable.BootstrapButton_bb_text_gravity);
		}
		
		boolean enabled = true;
		if(a.getString(R.styleable.BootstrapButton_android_enabled) != null) {
			enabled = a.getBoolean(R.styleable.BootstrapButton_android_enabled, true);
		}
		
		int layoutWidth = 0;
		if(a.getString(R.styleable.BootstrapButton_android_layout_width) != null) {
			layoutWidth = a.getInt(R.styleable.BootstrapButton_android_layout_width, 0);
		}
		
		//works even if it's fill_parent or match_parent 
		if( (layoutWidth == LayoutParams.MATCH_PARENT)) {
			fillparent = true;
		}
		
		if(a.getString(R.styleable.BootstrapButton_android_textSize) != null) {
			
			//font sizes
			String xmlProvidedSize = attrs.getAttributeValue(
					"http://schemas.android.com/apk/res/android", "textSize");
			final Pattern PATTERN_FONT_SIZE = Pattern
					.compile("([0-9]+[.]?[0-9]*)sp");
			Matcher m = PATTERN_FONT_SIZE.matcher(xmlProvidedSize);

			if (m.find()) {

				if (m.groupCount() == 1) {

					fontSize = Float.valueOf(m.group(1));
				}

			}

		}
		
		a.recycle();
		View v;
		if(fillparent){
			v = inflator.inflate(R.layout.bootstrap_button_fill, null, false);
		} else {
			 v = inflator.inflate(R.layout.bootstrap_button, null, false);
		}
		
		
		//set up font sizes and padding for different button sizes
		if(size.equals("large")){
			fontSize = 20.0f;
			paddingA = (int) (15 *scale + 0.5f);
			paddingB = (int) (20 *scale + 0.5f);
		}
		
		if(size.equals("small")){
			fontSize = 12.0f;
			paddingA = (int) (5 *scale + 0.5f);
			paddingB = (int) (10 *scale + 0.5f);
		}
		
		if(size.equals("xsmall")){
			fontSize = 10.0f;
			paddingA = (int) (2 *scale + 0.5f);
			paddingB = (int) (5 *scale + 0.5f);
		}
	
		//get layout items
		layout = (ViewGroup) v.findViewById(R.id.layout);
		lblLeft = (TextView) v.findViewById(R.id.lblLeft);
		lblMiddle = (TextView) v.findViewById(R.id.lblMiddle);
		lblRight = (TextView) v.findViewById(R.id.lblRight);

		//set the background
		//setBootstrapType(bootstrapType);
		
		//get the correct background type
		if(roundedCorners)
		{
			type = bbuttonTypeMapRounded.get(bootstrapType);
		} else {
			type = bbuttonTypeMap.get(bootstrapType);
		}
		
		//set up as default
		if (type == null)
		{
			type = BootstrapTypes.DEFAULT;
		}
	
		//apply the background type
		layout.setBackgroundResource(type.backgroundDrawable);
		lblLeft.setTextColor(getResources().getColor(type.textColour));
		lblMiddle.setTextColor(getResources().getColor(type.textColour));
		lblRight.setTextColor(getResources().getColor(type.textColour));
		
		//set the font awesome icon typeface
		lblLeft.setTypeface(font);
		lblRight.setTypeface(font);
		
		//set up the font size
		lblLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		lblMiddle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		lblRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
		
		//deal with gravity

		if(gravity.length() > 0) {
			setTextGravity(gravity);
		}


		boolean onlyIcon = true;

		//set the text
		if(text.length() > 0){
			lblMiddle.setText(text );
			lblMiddle.setVisibility(View.VISIBLE);
			onlyIcon = false;
		}

		//set up the padding

		if (iconLeft.length() > 0) {
			//lblLeft.setText(iconLeft);
			setLeftIcon(iconLeft);
			lblLeft.setVisibility(View.VISIBLE);

			if (onlyIcon){
				lblLeft.setPadding(paddingB, 0, 0, 0);
			} else {
				lblLeft.setPadding(paddingB, 0, paddingB, 0);
			}

			//padding for symmetry
			if ( ( iconRight.length() == 0) && !onlyIcon ) {
				lblMiddle.setPadding(paddingA, 0, paddingB, 0);
			}

		}

		if (iconRight.length() > 0) {
			//lblRight.setText(iconRight);
			setRightIcon(iconRight);
			lblRight.setVisibility(View.VISIBLE);

			if (onlyIcon){
				lblRight.setPadding(0, 0, paddingB, 0);
			}else {
				lblRight.setPadding(paddingB, 0, paddingB, 0);
			}

			//padding for symmetry
			if ( (iconLeft.length() == 0) && !onlyIcon) {
				lblMiddle.setPadding(paddingB, 0, paddingA, 0);
			}
		}

		if(iconLeft.length() > 0 && iconRight.length() > 0 )
		{
			lblMiddle.setPadding(paddingA, 0, paddingA, 0);
		}
		this.setClickable(true);

		this.setEnabled(enabled);

		layout.setPadding(0, paddingB, 0, paddingB);

		addView(v);
	}

	//static class to read in font
	private static void readFont(Context context)
	{
		
		if(font == null){	
			try {
			font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
			} catch (Exception e) {
				Log.e("BootstrapButton", "Could not get typeface because " + e.getMessage());
				font = Typeface.DEFAULT;
			}
		}

	}
	
	
	/**
	 * Changes the button text
	 * @param text - String value for what is displayed on the button
	 */
	public void setText(String text) {
		lblMiddle.setText(text);
	}
	

	/**
	 * Changes the left icon on a BootstrapButton
	 * @param leftIcon- String value for the icon as per http://fortawesome.github.io/Font-Awesome/cheatsheet/
	 */
	public void setLeftIcon(String leftIcon) {
		
		String icon = faMap.get(leftIcon);
		
		if (icon == null)
		{
			icon = faMap.get(FA_ICON_QUESTION);
		}
		
		lblLeft.setText(icon);
	}

	private boolean rotating = false;

    public void toggleRotation(final int whichIcon, boolean clockwise, AnimationSpeed speed) {
        this.rotating = !this.rotating;
        if (!this.rotating) {
            this.startRotateIcon(whichIcon, clockwise, speed);
        } else {
            this.stopAnimation();
        }
    }
	public void startRotateRightIcon(boolean clockwise, AnimationSpeed speed) {
		startRotateIcon(BB_ICON_RIGHT, clockwise, speed);
	}

	public void startRotateLeftIcon(boolean clockwise, AnimationSpeed speed) {
		startRotateIcon(BB_ICON_LEFT, clockwise, speed);
	}

	// this should be private and take which TextView to rotate since rotation center matters
	public void startRotateIcon(final int whichIcon, boolean clockwise, AnimationSpeed speed)
	{
		// choose which one to rotate
		boolean left = false, right = false;
		lblRight.measure(0, 0);       //must call measure!
		int h = lblRight.getMeasuredHeight();  //get width
		int w = lblRight.getMeasuredWidth();  //get height
		float iconMid = 0.25f;  //1.0f*h/w/2; // find a way to calculate it
		Log.e("Mid", ""+h+"_"+w+"|"+iconMid);
		if ((whichIcon & BB_ICON_LEFT) != 0 && lblLeft.getText().length() > 0) {
			left = true;
		}
		if ((whichIcon & BB_ICON_RIGHT) != 0 && lblRight.getText().length() > 0) {
			right = true;
		}
		if (!left && !right) { // nothing to roll!
			return;
		}

		final TextView tvL = lblLeft; // get text view of left side
		final TextView tvR = lblRight; // get text view of right side

		Animation rotate;
		
		//set up the rotation animation
		if (clockwise){
			rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, iconMid, Animation.RELATIVE_TO_SELF, 0.5f);
		} else {
			rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, iconMid, Animation.RELATIVE_TO_SELF, 0.5f);
		}
		
		//set up some extra variables
		rotate.setRepeatCount(Animation.INFINITE);
		rotate.setInterpolator(new LinearInterpolator());
		rotate.setStartOffset(0);
		rotate.setRepeatMode(Animation.RESTART);
		
		//defaults
		rotate.setDuration(2000);
		
		//fast
		if (speed.equals(AnimationSpeed.FAST))
		{
			rotate.setDuration(500);
		}

		//medium
		if (speed.equals(AnimationSpeed.MEDIUM))
		{
			rotate.setDuration(1000);
		}
		
		//send the new animation to a final animation
		final Animation animation = rotate;
		
		//run the animation - used to work correctly on older devices
        this.rotating = true;

		if (left) {
			tvL.postDelayed(new Runnable() {
				@Override
				public void run() {
					tvL.startAnimation(animation);
				}
			}, 100);
		}
		if (right) {
			tvR.postDelayed(new Runnable() {
				@Override
				public void run() {
					tvR.startAnimation(animation);
				}
			}, 100);
		}
	}
	
	// clear both animation
	public void stopAnimation(){
        this.rotating = false;
		//stop the animations
		if (lblLeft.getText().length() > 0) {
			lblLeft.clearAnimation();
		}
		if (lblRight.getText().length() > 0) {
			lblRight.clearAnimation();
		}
	}
	
	/**
	 * Changes the right icon on a BootstrapButton
	 * @param rightIcon - String value for the icon as per http://fortawesome.github.io/Font-Awesome/cheatsheet/
	 */
	public void setRightIcon(String rightIcon) {
		
		String icon = faMap.get(rightIcon);
		
		if (icon == null)
		{
			icon = faMap.get(FA_ICON_QUESTION);
		}
		
		lblRight.setText(icon);
		
	}
	
	/**
	 * Changes the type of BootstrapButton
	 * @param bootstrapType - String value for the type of button e.g. "primary"
	 */
	public void setBootstrapType(String bootstrapType) {

		BootstrapTypes type;
		
		//get the correct background type
		if (!roundedCorners) {
			type = bbuttonTypeMapRounded.get(bootstrapType);
		} else {
			type = bbuttonTypeMap.get(bootstrapType);
		}
		
		//set up as default
		if (type == null) {
			type = BootstrapTypes.DEFAULT;
		}
		
		
		layout.setBackgroundResource(type.backgroundDrawable);
		lblLeft.setTextColor(getResources().getColor(type.textColour));
		lblMiddle.setTextColor(getResources().getColor(type.textColour));
		lblRight.setTextColor(getResources().getColor(type.textColour));

	}
	
	/**
	 * Specifies whether the BootstrapButton is enabled or disabled
	 * @param enabled - boolean state for either enabled or disabled
	 */
	public void setBootstrapButtonEnabled(boolean enabled)
	{
		this.setEnabled(enabled);
	}
	
	
	/**
	 * Changes the gravity for the text on a bootstrap button that is not wrap_content
	 * @param gravity - string for either center, right, or left.
	 */
	public void setTextGravity(String gravity) {
		if(gravity.equals("left")) {
			lblMiddle.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		} else if (gravity.equals("center")) {
			lblMiddle.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
		} else if (gravity.equals("right")) {
			lblMiddle.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
		}
 
	}
}
