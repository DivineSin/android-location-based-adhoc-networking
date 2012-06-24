package edu.gmu.content;

import edu.gmu.ContextDataProvider;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ContextContentProvider extends ContentProvider {

	private ContextDataProvider db;
	
	public static final UriMatcher sUriMatcher;
	
	private static final int ALL_PEOPLE = 1;
	private static final int SINGLE_PERSON = 2;
	
	private static final int ALL_VEHICLES = 3;
	private static final int SINGLE_VEHICLE = 4;
	
	private static final int ALL_LANDMARKS = 5;
	private static final int SINGLE_LANDMARK = 6;
	
	private static final int ALL_RESOURCES = 7;
	private static final int SINGLE_RESOURCE = 8;
	
	private static final int ALL_OBJECTIVES = 9;
	private static final int SINGLE_OBJECTIVE = 10;
	
	private static final int PERSON_LOCATION = 11;
	private static final int VEHICLE_LOCATION = 12;
	private static final int LANDMARK_LOCATION = 13;
	private static final int RESOURCE_LOCATION = 14;
	
	private static final int PATROL_OBJECTIVE = 15;
	private static final int HUMANITARIAN_OBJECTIVE = 16;
	private static final int PATROL_OBJECTIVE_ROUTE = 17;
	private static final int HUMANITARIAN_OBJECTIVE_ROUTE = 19;	
	private static final int VEHICLE_OBJECTIVE_RELEVANCE = 18;
	
	public static final String baseURI = "edu.gmu.provider";
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(baseURI + ".cursor.dir","people",ALL_PEOPLE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","person/#",SINGLE_PERSON);
		sUriMatcher.addURI(baseURI + ".cursor.dir","person/location",PERSON_LOCATION);
		sUriMatcher.addURI(baseURI + ".cursor.dir","vehicles",ALL_VEHICLES);
		sUriMatcher.addURI(baseURI + ".cursor.dir","vehicle/#",SINGLE_VEHICLE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","vehicle/location",VEHICLE_LOCATION);
		
		sUriMatcher.addURI(baseURI + ".cursor.dir","landmarks",ALL_LANDMARKS);
		sUriMatcher.addURI(baseURI + ".cursor.dir","landmark/#",SINGLE_LANDMARK);
		sUriMatcher.addURI(baseURI + ".cursor.dir","landmark/location",LANDMARK_LOCATION);
		
		sUriMatcher.addURI(baseURI + ".cursor.dir","resources",ALL_RESOURCES);
		sUriMatcher.addURI(baseURI + ".cursor.dir","resource/#",SINGLE_RESOURCE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","resource/location",RESOURCE_LOCATION);
		
		sUriMatcher.addURI(baseURI + ".cursor.dir","objective/patrol",PATROL_OBJECTIVE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","objective/humanitarian",HUMANITARIAN_OBJECTIVE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","objective/patrol/route",PATROL_OBJECTIVE_ROUTE);
		sUriMatcher.addURI(baseURI + ".cursor.dir","objective/humanitarian/route",HUMANITARIAN_OBJECTIVE_ROUTE);
		
		sUriMatcher.addURI(baseURI + ".cursor.dir","objective/vehicle_objective_relevance/#",VEHICLE_OBJECTIVE_RELEVANCE);
		
//		sUriMatcher.addURI(baseURI,"vehicle",ALL_VEHICLES);
//		sUriMatcher.addURI(baseURI,"vehicle/#",SINGLE_VEHICLE);
//		sUriMatcher.addURI(baseURI,"landmark",ALL_LANDMARKS);
//		sUriMatcher.addURI(baseURI,"landmark/#",SINGLE_LANDMARK);
//		sUriMatcher.addURI(baseURI,"objective",ALL_OBJECTIVES);
//		sUriMatcher.addURI(baseURI,"objective/#",SINGLE_OBJECTIVE);
	}
	//content://edu.gmu.provider.cursor.dir/people
	
	public static final String PEOPLE_CONTENT_TYPE = "content://edu.gmu.cursor.dir/people";
	public static final String PERSON_CONTENT_TYPE = "content://edu.gmu.cursor.item/person";
	public static final String PERSON_LOCATION_CONTENT_TYPE = "content://edu.gmu.cursor.item/person/location";
	
	public static final String VEHICLES_CONTENT_TYPE = "content://edu.gmu.cursor.dir/vehicles";
	public static final String VEHICLE_CONTENT_TYPE = "content://edu.gmu.cursor.item/vehicle";
	public static final String VEHICLE_LOCATION_CONTENT_TYPE = "content://edu.gmu.cursor.item/vehicle/location";
	
	public static final String LANDMARKS_CONTENT_TYPE = "content://edu.gmu.cursor.dir/landmarks";
	public static final String LANDMARK_CONTENT_TYPE = "content://edu.gmu.cursor.item/landmark";
	
	public static final String SPACE_TIME_CONTENT_TYPE = "content://edu.gmu.cursor.item/spacetime";
	public static final String LOCATION_CONTENT_TYPE = "content://edu.gmu.cursor.item/location";
	
//	public static final String LANDMARKS_CONTENT_TYPE = "content://edu.gmu.landmark.cursor.item/landmarks";
//	public static final String LANDMARK_CONTENT_TYPE = "content://edu.gmu.landmark.cursor.item/landmark";
	
	public static final String OBJECTIVE_CONTENT_TYPE = "content://edu.gmu.cursor.item/objective";
	public static final String OBJECTIVE_ROUTE_CONTENT_TYPE = "content://edu.gmu.cursor.dir/objective/route";
	
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new IllegalArgumentException ("Unsupported URI:" + arg0);
	}

	@Override
	public String getType(Uri arg0) {
		switch(sUriMatcher.match(arg0)) {
		case ALL_PEOPLE:
			return PEOPLE_CONTENT_TYPE;
		case SINGLE_PERSON:
			return PERSON_CONTENT_TYPE;
		case ALL_VEHICLES:
			return VEHICLES_CONTENT_TYPE;
		case SINGLE_VEHICLE:
			return VEHICLE_CONTENT_TYPE;
		case ALL_LANDMARKS:
			return LANDMARKS_CONTENT_TYPE;
		case SINGLE_LANDMARK:
			return LANDMARK_CONTENT_TYPE;
		case SINGLE_OBJECTIVE:
			return OBJECTIVE_CONTENT_TYPE;
		case PERSON_LOCATION:
			return PERSON_LOCATION_CONTENT_TYPE;
		case VEHICLE_LOCATION:
			return VEHICLE_LOCATION_CONTENT_TYPE;
		case PATROL_OBJECTIVE_ROUTE:
			return OBJECTIVE_ROUTE_CONTENT_TYPE;
		}
		return null;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long row;
		Uri ret = null;
		switch (sUriMatcher.match(uri)) {
//		case SINGLE_PERSON:
//			row = db.insertPerson(values);
//			ret = ContentUris.withAppendedId(Uri.parse("content:" + PERSON_CONTENT_TYPE), row);
//			break;
		default:
			throw new IllegalArgumentException("Unkown URI:" + uri);
		}
		
//		return ret;
	}

	@Override
	public boolean onCreate() {
		db = new ContextDataProvider(this.getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor ret;
		switch (sUriMatcher.match(uri)) {
		case ALL_PEOPLE:
			ret = db.getPeople();
			break;
		case PERSON_LOCATION:
			ret = db.getCurrentLocationForPerson(Long.parseLong(selectionArgs[0]));
			break;
		case ALL_VEHICLES:
			ret = db.getVehicles();
			break;
		case VEHICLE_LOCATION:
			ret = db.getCurrentLocationForVehicle(Long.parseLong(selectionArgs[0]));
			break;
		case ALL_LANDMARKS:
			ret = db.getLandmarks();
			break;
		case LANDMARK_LOCATION:
			ret = db.getCurrentLocationForLandmark(Long.parseLong(selectionArgs[0]));
			break;
		case ALL_RESOURCES:
			ret = db.getResources();
			break;
		case RESOURCE_LOCATION:
			ret = db.getCurrentLocationForResource(Long.parseLong(selectionArgs[0]));
			break;
		case PATROL_OBJECTIVE:
			ret = db.getPatrolObjective();
			break;
		case PATROL_OBJECTIVE_ROUTE:
			ret = db.getCurrentLocationForObjective(Long.parseLong(selectionArgs[0]));
			break;
		case HUMANITARIAN_OBJECTIVE:
			ret = db.getHumanitarianObjective();
			break;
		case HUMANITARIAN_OBJECTIVE_ROUTE:
			ret = db.getCurrentLocationForObjective(Long.parseLong(selectionArgs[0]));
			break;
		default:
			throw new IllegalArgumentException("Unkown URI:" + uri);
		}
		
		ret.setNotificationUri(getContext().getContentResolver(), uri);
		return ret;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

}
