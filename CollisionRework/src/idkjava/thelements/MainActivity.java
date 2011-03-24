package idkjava.thelements;

import idkjava.thelements.game.Control;
import idkjava.thelements.game.FileManager;
import idkjava.thelements.game.MenuBar;
import idkjava.thelements.game.SandView;
import idkjava.thelements.preferences.PreferencesActivity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

public class MainActivity extends Activity
{
	//Constants for zoom
	public static final boolean ZOOMED_IN = true;
	public static final boolean ZOOMED_OUT = false;
	
	//Constants for dialogue ids
	private static final int INTRO_MESSAGE = 1;
	private static final int ELEMENT_PICKER = 2;
	private static final int BRUSH_SIZE_PICKER = 3;
	
	//Constants for elements
	public static final char ERASER_ELEMENT = 2;
	public static final char NORMAL_ELEMENT = 3;
	
	//Constants for intents
	public static final char SAVE_STATE_ACTIVITY = 0;
	
	//Request code constants
	public static final int REQUEST_CODE_SELECT_SAVE = 0;
	
	static CharSequence[] elementslist;

	public static boolean play = true;
	public static boolean zoomState = ZOOMED_IN; //Zoomed in or not

	private SensorManager mSensorManager;

	public static final String PREFS_NAME = "MyPrefsfile";
	public static boolean shouldLoadDemo = false;

	public static boolean ui;

	public static MenuBar menu_bar;
	public static Control control;
	public static SandView sand_view;

	private SensorManager myManager;
	private List<Sensor> sensors;
	private Sensor accSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Uses onCreate from the general Activity
		super.onCreate(savedInstanceState);

		//Get rid of title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Initialize most of the arrays in JNI
		nativeInit();
		
		//Set the preferences
		PreferencesActivity.setPreferences(this);

		//Set Sensor + Manager
		myManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accSensor = myManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		setUpViews();

		//Set up the elements list
		Resources res = getResources();
		elementslist = res.getTextArray(R.array.elements_list);
		
		//Load the custom elements
		//CustomElementManager.reloadCustomElements();
		
		//Add custom elements to the elements list
	}

	
	private final SensorEventListener mySensorListener = new SensorEventListener()
	{
		//accsensor registered in onResume()
		public void onSensorChanged(SensorEvent event)
		{
			//native setGravity methods called
			setXGravity(event.values[0]);
			setYGravity(event.values[1]);
			//Log.e("The Elements", "Gravity Change, X: " + new Float(event.values[0]).toString() + 
				//	" Y: " + new Float(event.values[1]).toString());
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
		}
	};
	
	
	@Override
	protected void onPause()
	{
		//Do a temp save
		saveTempState();
		//Set the preferences to indicate paused
		SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
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
		boolean oldui = ui;
		PreferencesActivity.setPreferences(this);
		if(ui != oldui)
		{
			Log.v("TheElements", "UI was changed");
			setUpViews();
		}
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		
		myManager.registerListener(mySensorListener, accSensor, SensorManager.SENSOR_DELAY_GAME);
		
		//If we're resuming from a pause (not when it starts)
		if (settings.getBoolean("paused", false))
		{
			//Load the save
			//TODO: loadTempState();
			//Set the preferences to indicate unpaused
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("paused", false);
			editor.commit();
		}
		
		//Set up the file manager for saving and loading
		FileManager.intialize(this);
		
		//First run
		if (settings.getBoolean("firstrun", true))
		{
			shouldLoadDemo = true;
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("firstrun", false);
			editor.commit();

			showDialog(INTRO_MESSAGE); //Pop up intro message
		}

		if (ui)
		{
			//This is where I set the activity for Control so that I can call showDialog() from it
			control.setActivity(this);
			//Set instance of activity for MenuBar also
			menu_bar.setActivity(this);
		}

		//Use the super onResume
		super.onResume();
		//Call onResume() for view too
		sand_view.onResume();
	}

	protected Dialog onCreateDialog(int id) //This is called when showDialog is called
	{
		if (id == INTRO_MESSAGE) // The first dialog - the intro message
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.app_intro).setCancelable(false).setPositiveButton(R.string.exit, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					finish();
				}
			}).setNegativeButton(R.string.proceed, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create(); // Actually create the message
			return alert; // Return the object created
		}
		else if (id == ELEMENT_PICKER) // Element picker
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // Create a new one
			builder.setTitle(R.string.element_picker); // Set the title
			builder.setItems(elementslist, new DialogInterface.OnClickListener() //Create the list
			{
				public void onClick(DialogInterface dialog, int item)
				{
					if (MenuBar.eraserOn)
					{
						MenuBar.setEraserOff();
					}
					setElement((char) (item + NORMAL_ELEMENT));
				}
			});
			AlertDialog alert = builder.create(); // Create the dialog

			return alert; // Return handle
		}
		else if (id == BRUSH_SIZE_PICKER)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this); // Declare the object
			builder.setTitle(R.string.brush_size_picker);
			builder.setItems(R.array.brush_size_list, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int item)
				{
					if(item == 0)
					{
						setBrushSize((char) 0);
					}
					else
					{
						setBrushSize((char) java.lang.Math.pow(2, item-1));
					}
				}
			});
			AlertDialog alert = builder.create(); // Create object
			return alert; // Return handle
		}

		return null; //Default case: return nothing
	}

	public boolean onPrepareOptionsMenu(Menu menu) // Pops up when you press Menu
	{
		// Create an inflater to inflate the menu already defined in res/menu/options_menu.xml
		// This seems to be a bit faster at loading the menu, and easier to modify
		MenuInflater inflater = getMenuInflater();
		if (ui)
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
			{
				showDialog(ELEMENT_PICKER);
				return true;
			}
			case R.id.brush_size_picker:
			{
				showDialog(BRUSH_SIZE_PICKER);
				return true;
			}
			case R.id.clear_screen:
			{
				clearScreen();
				return true;
			}
			case R.id.play_pause:
			{
				play = !play;
				setPlayState(play);
				return true;
			}
			case R.id.eraser:
			{
				setElement(ERASER_ELEMENT);
				return true;
			}
			case R.id.toggle_size:
			{
				zoomState = !zoomState;
				setZoomState(zoomState);
				return true;
			}
			case R.id.save:
			{
				saveState();
				return true;
			}
			case R.id.load:
			{
				loadState();
				return true;
			}
			case R.id.load_demo:
			{
				loadDemoState();
				return true;
			}
			case R.id.preferences:
			{
				startActivity(new Intent(MainActivity.this, PreferencesActivity.class));
				return true;
			}
			case R.id.exit:
			{
				System.exit(0);
				return true;
			}
		}
		return false;
	}
	
	//Set up the views based on the state of ui
	private void setUpViews()
	{
		//Set the content view based on this variable
		if(ui)
		{
			Log.v("TheElements", "setcontentview - ui");
			setContentView(R.layout.ui);
		}
		else
		{
			Log.v("TheElements", "setcontentview - non_ui");
			setContentView(R.layout.non_ui);
		}

		//Set the new view and control box and menu bar to the stuff defined in layout
		menu_bar = (MenuBar) findViewById(R.id.menu_bar);
		sand_view = (SandView) findViewById(R.id.sand_view);
		control = (Control) findViewById(R.id.control);
		
		//Set the screen state for sand_view now that it's defined
		PreferencesActivity.setScreenState(this);
	}
	
	//Save the current state
	public void saveState()
	{
		Intent tempIntent = new Intent(this, SaveStateActivity.class);
		startActivity(tempIntent);
	}
	public void loadState()
	{
	 	Intent tempIntent = new Intent(this, LoadStateActivity.class);
	 	startActivity(tempIntent);
	}
	
	//Check whether or not the game is zoomed in
	public static boolean zoomedIn()
	{
		return zoomState;
	}

	//JNI Functions
	//Save/load functions
	public static native char saveTempState();
	public static native char loadTempState();
	public static native char loadDemoState();
	public static native char removeTempSave();
	
	//General utility functions
    public static native void nativeInit();
	public static native void clearScreen();
	
	//Setters
	public static native void setPlayState(boolean playState);
	public static native void setZoomState(boolean zoomState);
	public static native void setElement(char element);
	public static native void setBrushSize(char brushSize);
	
	//Getters
	public static native char getElement();
	
	//Acclerometer related
	public static native void setXGravity(float xGravity);
	public static native void setYGravity(float yGravity);
	
	//TODO: Network related
	public static native void setUsername(char[] username);
	public static native void setPassword(char[] password);
	public static native char login();
	public static native char register();
	public static native void viewErr(); //TODO: Figure this out

	static
	{
		System.loadLibrary("thelements"); // Load the JNI library (libthelements.so)
	}
}
