/*
 * collisions.h
 * -------------------------------
 * Contains all the collision data in a table. At some
 * point hopefully we can make some code that will load
 * this collision data from a file. When adding an element,
 * it must be added here.
 */

#define TElements 25

int collision[TElements][TElements] =
{
		//Sand 0
		{0, 28, 0, -1, 0, 27, 0, 10, 0, 1, 27, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Water 1
		{28, 3, 1, -1, 4, 23, 8, 10, 1, 3, 12, 1, 1, 1, 1, 1, -1, 19, 20, 24, 3, 1, 1, 1, 1},
		//Wall 2
		{0, 1, 0, -1, 0, 5, 0, 10, 0, 1, 0, 0, 0, 0, 0, 0, -1, 1, 22, 0, 1, 0, 0, 0, 0},
		//Eraser 3
		{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		//Plant 4
		{0, 1, 0, -1, 0, 6, 0, 10, 0, 1, 6, 0, 0, 0, 0, 0, -1, 18, 22, 0, 26, 0, 0, 0, 0},
		//Fire 5
		{5, 23, 5, -1, 6, 7, 9, 10, 5, 11, 5, 13, 15, 16, 6, 5, -1, 5, 22, 5, 23, 5, 5, 29, 5},
		//Ice 6
		{0, 8, 0, -1, 0, 9, 0, 10, 0, 1, 9, 0, 0, 0, 0, 0, -1, 18, 20, 25, 25, 0, 0, 0, 0},
		//Generator 7
		{10, 10, 10, -1, 10, 10, 10, 0, 0, 10, 10, 10, 10, 10, 10, 10, -1, 10, 10, 10, 10, 10, 10, 10, 10},
		//Spawn 8
		{0, 1, 0, -1, 0, 5, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, -1, 1, 22, 0, 1, 0, 0, 0, 0},
		//Oil 9
		{1, 3, 1, -1, 1, 11, 1, 10, 1, 3, 11, 1, 1, 1, 1, 1, -1, 18, 22, 1, 3, 1, 1, 1, 1},
		//Magma 10
		{27, 12, 1, -1, 6, 5, 9, 10, 1, 11, 3, 13, 15, 16, 6, 17, -1, 18, 22, 1, 12, 1, 1, 30, 1},
		//Stone 11
		{0, 1, 0, -1, 0, 13, 0, 10, 0, 1, 13, 14, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//C4 12
		{0, 1, 0, -1, 0, 15, 0, 10, 0, 1, 15, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//C4++ 13
		{0, 1, 0, -1, 0, 16, 0, 10, 0, 1, 16, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Fuse 14
		{0, 1, 0, -1, 0, 6, 0, 10, 0, 1, 6, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Destructable Wall 15
		{0, 1, 0, -1, 0, 5, 0, 10, 0, 1, 17, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Wind 16
		{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
		//Acid 17
		{18, 19, 1, -1, 18, 5, 18, 10, 1, 18, 18, 18, 18, 18, 18, 18, -1, 3, 22, 18, 18, 1, 18, 18, 18},
		//Steam 18
		{22, 20, 22, -1, 22, 5, 20, 10, 22, 22, 22, 22, 22, 22, 22, 22, -1, 22, 21, 22, 20, 22, 22, 22, 22},
		//Salt 19
		{0, 24, 0, -1, 0, 5, 25, 10, 0, 1, 1, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Salt-water 20
		{1, 3, 1, -1, 26, 23, 25, 10, 1, 3, 12, 1, 1, 1, 1, 1, -1, 18, 20, 1, 3, 0, 1, 1, 1},
		//Glass 21
		{0, 1, 0, -1, 0, 5, 0, 10, 0, 1, 1, 0, 0, 0, 0, 0, -1, 1, 22, 0, 1, 0, 0, 0, 0},
		//Custom 1 22
		{0, 1, 0, -1, 0, 27, 0, 10, 0, 1, 27, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Mud 2 23
		{0, 1, 0, -1, 0, 29, 0, 10, 0, 1, 30, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0},
		//Custom 3 24
		{0, 1, 0, -1, 0, 27, 0, 10, 0, 1, 27, 0, 0, 0, 0, 0, -1, 18, 22, 0, 1, 0, 0, 0, 0}
};