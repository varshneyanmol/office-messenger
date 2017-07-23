package com.app.server;

import java.util.ArrayList;
import java.util.Collections;

/**
 * getUniqueIdentifier() returns a unique random between [0, RANGE-1].
 */
public class UniqueIdentifier {
	private static ArrayList<Integer> group_ids = new ArrayList<Integer>();
	private static final int RANGE = 1000;
	private static int gindex = 0;

	static {
		for (int i = 0; i < RANGE; i++) {
			group_ids.add(i);
		}
		Collections.shuffle(group_ids);
	}

	private UniqueIdentifier() {
	}

	public static int getUniqueGroupIdentifier() {
		if (gindex >= group_ids.size())
			gindex = 0;
		return group_ids.get(gindex++);
	}
}
