/*
 * Element Works: Copyright (C) 2010 IDKJava
 * ----------------------------------------------------------
 * A sandbox type game in which you can play with different elements
 * which interact with each other in unique ways.
 */

package sand.falling.opengl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import sand.falling.opengl.preferences.PreferencesFromCode;
import sand.falling.opengl.custom.CustomMaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle; //import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class DemoActivity extends Activity
{
	public static final CharSequence[] elementslist = {"Sand", "Water", "Plant", "Wall", "Fire", "Ice", "Generator", "Oil", "Magma", "Stone", "C4", "C4++", "Fuse", "Destructible Wall", "Drag", "Acid", "Steam", "Salt", "Salt Water", "Glass", "Custom Element", "Mud"};
	static final CharSequence[] brushlist = {"1", "2", "4", "8", "16", "32"};

	static final int maxy = 414; // 454 for g1, 815 for droid
	static final int maxx = 319; // 319 for g1, 479 for droid
	static public boolean play = true;
	static public int speed = 1;
	static public int skip = 1;
	static public int size = 0;

	private SensorManager mSensorManager;

	public static final String PREFS_NAME = "MyPrefsfile";
	public static boolean loaddemov = false;

	public static boolean ui;
	public static boolean layout_ui;

	public static MenuBar menu_bar;
	public static Control control;
	public static SandView sand_view;

	private SensorManager myManager;
	private List<Sensor> sensors;
	private Sensor accSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState); //Uses onCreate from the general Activity

		requestWindowFeature(Window.FEATURE_NO_TITLE); //Get rid of title bar

		PreferencesFromCode.setpreferences(this);

		//Set Sensor + Manager
		myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accSensor = myManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		//Set the layout based on the settings
		if (ui)
		{
			setContentView(R.layout.ui);
		}
		else
		{
			setContentView(R.layout.non_ui);
		}

		//Sync on startup (layout_ui is not changed on preference changed for smoothness)
		layout_ui = ui;

		//Need to do this otherwise it gives a nullpointerexception
		menu_bar = new MenuBar(this, null);
		sand_view = new SandView(this, null);
		control = new Control(this, null);

		//Set the new view and control box and menu bar to the stuff defined in layout
		menu_bar = (MenuBar) findViewById(R.id.menu_bar);
		sand_view = (SandView) findViewById(R.id.sand_view);
		control = (Control) findViewById(R.id.control);

		PreferencesFromCode.setScreenOnOff(this); //Finds out to keep screen on or off

		CustomMaker.loadCustom(); //Load the custom elements
	}

	private final SensorEventListener mySensorListener = new SensorEventListener()
	{
		public void onSensorChanged(SensorEvent event)
		{
			sendxg(event.values[0]);
			sendyg(event.values[1]);
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}
	};

	@Override
	protected void onPause()
	{
		//Quicksave
		quicksave();
		//Set the preferences to indicate paused
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("paused", true);
		editor.commit();
		//Use the normal onPause
		super.onPause();
		//Call onPause for the view
		sand_view.onPause();
	}

	@Override
	protected void onResume()
	{
		if (layout_ui != ui)
		{
			System.exit(0);
		}
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);

		//If we're resuming from a pause (not when it starts)
		if (settings.getBoolean("paused", true))
		{
			//Load the save
			quickload();
			//Set the preferences to indicate unpaused
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("paused", false);
			editor.commit();
		}

		//Set firstrun
		boolean firstrun = settings.getBoolean("firstrun", true);
		
		//Input the correct save file (based on screen width)
		InputStream in;
		if (sand_view.getWidth() < 300)
		{
			in = getResources().openRawResource(R.raw.save);
		}
		else
		{
			in = getResources().openRawResource(R.raw.droiddemo);
		}

		try
		{
			//Try to create the folder
			boolean success = (new File("/sdcard/elementworks/")).mkdir();

			//Try to copy the demo file to the save file
			OutputStream out = new FileOutputStream("/sdcard/elementworks/save2.txt");
			byte[] buf = new byte[100024];
			int len;
			while ((len = in.read(buf)) != -1)
			{
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
		catch (FileNotFoundException e)
		{
			//FileNotFoundException is normal, ignore it
			e.printStackTrace();
		}
		catch (IOException e)
		{
			//IOException is also fine, ignore
			e.printStackTrace();
		}
		
		//If it's the first run, tell it to load the demo and unset firstrun
		if (firstrun == true)
		{
			loaddemov = true;
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("firstrun", false);
			editor.commit();

			showDialog(1); //Pop up intro message
		}

		if (layout_ui)
		{
			// This is where I set the activity for Control so that I can call showDialog() from it
			control.setActivity(this);
			//Set instance of activity for MenuBar also
			menu_bar.setActivity(this);
		}
		
		//Use the super onResume as well
		super.onResume();
		//Call onResume() for view too
		sand_view.onResume();
	}

	protected Dialog onCreateDialog(int id) //This is called when showDialog is called
	{
		if (id == 1) // The first dialog - the intro message
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.App_Intro).setCancelable(false).setPositiveButton("Exit", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					DemoActivity.this.finish(); // Exit button
					// closes
					// program
				}
			}).setNegativeButton("Proceed", new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel(); // Proceed closes the
					// message
				}
			});
			AlertDialog alert = builder.create(); // Actually create the message
			return alert; // Return the object created
		}
		else if (id == 2) // Element picker
		{
 			
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create a new one
			builder.setTitle("Pick an element"); // Set the title
			builder.setItems(elementslist, new DialogInterface.OnClickListener() //Create the list
			{
				public void onClick(DialogInterface dialog, int item)
				{
					if (MenuBar.eraser_on == true)
					{
							MenuBar.seteraseroff();
					}

					if (item == 0) // Sand
					{
						setelement(0);
					}
					else if (item == 1) // Water
					{
						setelement(1);
					}
					else if (item == 2) // Plant
					{
						setelement(4);
					}
					else if (item == 3) // Wall
					{
						setelement(2);
					}
					else if (item == 4) // Fire
					{
						setelement(5);
					}
					else if (item == 5) // Ice
					{
						setelement(6);
					}
					else if (item == 6)// Generator
					{
						setelement(7);
					}
					else if (item == 7)// Oil
					{
						setelement(9);
					}
					else if (item == 8)// Magma
					{
						setelement(10);
					}
					else if (item == 9)// Stone
					{
						setelement(11);
					}
					else if (item == 10)// C4
					{
						setelement(12);
					}
					else if (item == 11)// C4++
					{
						setelement(13);
					}
					else if (item == 12)// Fuse
					{
						setelement(14);
					}
					else if (item == 13)// Destructible wall
					{
						setelement(15);
					}
					else if (item == 14)// Drag
					{
						setelement(16);
					}
					else if (item == 15)// Acid
					{
						setelement(17);
					}
					else if (item == 16)// Steam
					{
						setelement(18);
					}
					else if (item == 17)// Salt
					{
						setelement(19);
					}
					else if (item == 18)// Salt-water
					{
						setelement(20);
					}
					else if (item == 19)// Glass
					{
						setelement(21);
					}
					else if (item == 20)// Custom 1
					{
						setelement(22);
					}
					else if (item == 21)// Mud
					{
						setelement(23);
					}
					else if (item == 22)// Custom 3
					{
						setelement(24);
					}
				}
			});
			AlertDialog alert = builder.create(); // Create the dialog

			return alert; // Return handle
		}
		else if (id == 3)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // Declare the object
			builder.setTitle("Brush Size Picker");
			builder.setItems(brushlist, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item)
				{
					setBrushSize((int) java.lang.Math.pow(2, item));
				}
			});
			AlertDialog alert = builder.create(); // Create object
			return alert; // Return handle
		}

		return null; // No need to return anything, just formality
	}

	public boolean onPrepareOptionsMenu(Menu menu) // Pops up when you press Menu
	{
		// Create an inflater to "inflate" the menu already defined in res/menu/options_menu.xml
		// This seems to be a bit faster at loading the menu, and easier to modify
		MenuInflater inflater = getMenuInflater();
		if (layout_ui)
		{
			menu.clear();
			inflater.inflate(R.menu.options_menu_small, menu);
		}
		else
		{
			menu.clear();
			inflater.inflate(R.menu.options_menu_large, menu);
		}

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.element_picker:
	
				showDialog(2);
	
				return true;
			case R.id.brush_size_picker:
	
				showDialog(3);
	
				return true;
			case R.id.clear_screen:
	
				setup();
	
				return true;
			case R.id.play_pause:
	
				if (play)
				{
					jPause();
					play = false;
				}
				else
				{
					Play();
					play = true;
				}
				return true;
			case R.id.eraser:
	
				setelement(3);
				return true;
			case R.id.toggle_size:
	
				if (size == 1)
				{
					size = 0;
				}
				else
				{
					size = 1;
				}
				togglesize();
				return true;
			case R.id.save:
	
				save();
				return true;
			case R.id.load:
	
				load();
				return true;
			case R.id.load_demo:
	
				loaddemo();
				return true;
			case R.id.preferences:
	
				startActivity(new Intent(DemoActivity.this, PreferencesFromCode.class));
				return true;
			case R.id.exit:
	
				System.exit(0);
				return true;
		}
		return false;
	}

	// JNI functions
	public native static int save();
	public native static int loaddemo();
	public native static int load();
	public native static void setup(); //Set up arrays and such
	public native static void fd(int fstate); //Sets finger up or down, 1 is down
	public native static void mp(int jxm, int jym); //Sets x mouse and y mouse
	public native static void tester();
	public native static void Play(); // Jni play
	public native static void jPause(); // Jni pause
	public native static int  getPlayState(); //Get the play state
	public native static void togglesize(); // Jni toggle size
	public native static void quicksave();
	public native static void quickload();
	public native static void setBackgroundColor(int colorcode);
	public native static void setFlip(int flipped);
	public native static void setelement(int element);
	public native static void setBrushSize(int jsize);
	public native static int  getelement();
	public native static void clearquicksave();
	public native static void sendyg(float ygrav);
	public native static void sendxg(float xgrav);
	public native static void setAccelOnOff(int state);
	public native static void setcollision(int custnumber, int elementnumb, int collisionspot, int collisionnumber);
	public native static void setexplosiveness(int explosiveness);
	public native static void setred(int redness);
	public native static void setblue(int blueness);
	public native static void setgreen(int greenness);
	public native static void setdensity( int jdensity );
	public native static void setUserName( char[] username);
	public native static void setPassword ( char[] password);
	public native static boolean login();
	public native static boolean register();

	static
	{
		System.loadLibrary("thelements"); // Load the JNI library (libsanangeles.so)
	}
}