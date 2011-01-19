/*
 * saveload.h
 * --------------------------
 * Declares the function definitions for saver
 * and loader, the two functions which save and
 * load an element setup. Also defines macros
 * for file locations, and includes needed headers.
 */

#ifndef SAVELOAD_H_INCLUDED
#define SAVELOAD_H_INCLUDED

//File/folder locations
#define ROOT_FOLDER "/sdcard/thelements/"
#define SAVES_FOLDER "/sdcard/thelements/saves/"
#define CUSTOM_ELEMENTS_FOLDER "/sdcard/thelements/elements/"
#define ATMOSPHERES_FOLDER "/sdcard/thelements/atmospheres/"
#define TEMP_SAVE_FILE "temp"
#define DEMO_SAVE_FILE "demo"
//File extensions
#define SAVE_EXTENSION ".sav"
#define ELEMENT_EXTENSION ".ele"
#define ATMOSPHERE_EXTENSION ".atm"

//Include the FILE type
#include <stdio.h>
//Include the date and time functions
#include <time.h>
//Include the global variables
#include "app.h"
//Include the global macros
#include "macros.h"
//Include points functions
#include "points.h"
//Include the initializer function
#include "setup.h"

char saveState(int type);
char loadState(int type, char* filename);
void removeTempSave(void);
void saveCustomElement(struct Element* createdCustomElement);
void loadCustomElements(void);
void saveAtmosphere(struct Atmosphere* createAtmosphere);
void loadAtmosphere(void);
int getCustomElementsNumber(void);

#endif //!SAVELOAD_H_INCLUDED