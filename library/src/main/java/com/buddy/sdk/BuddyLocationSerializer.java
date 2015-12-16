package com.buddy.sdk;

import android.location.Location;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.util.Locale;

public class BuddyLocationSerializer implements JsonSerializer<Location>{

	public static String serializeCore(Location location) {
		return String.format(Locale.US,"%f,%f",location.getLatitude(),location.getLongitude());
	}

    @Override public JsonElement serialize(final Location location, final Type typeOfSrc, final JsonSerializationContext context) {
        
        String locString = serializeCore(location);

        JsonElement result = new JsonPrimitive(locString);
        return result;
    }
}
