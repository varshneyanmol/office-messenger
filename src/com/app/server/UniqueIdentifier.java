package com.app.server;

import java.util.ArrayList;
import java.util.Collections;

/**
 * getUniqueIdentifier() returns a unique random between [1, RANGE]. groupID '0'
 * represents broadcast group.
 */
public class UniqueIdentifier {
	private static ArrayList<Integer> group_ids = new ArrayList<Integer>();
	private static final int RANGE = 1000;
	private static int gindex = 0;

	static {
		for (int i = 1; i <= RANGE; i++) {
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
