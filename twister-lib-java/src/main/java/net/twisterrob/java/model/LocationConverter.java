package net.twisterrob.java.model;
// TODO https://groups.google.com/forum/#!topic/Google-Maps-API/6459F-hBMqc
// TODO http://www.uwgb.edu/dutchs/UsefulData/UTMFormulas.HTM
// TODO http://www.uwgb.edu/dutchs/UsefulData/ConvertUTMNoOZ.HTM
public class LocationConverter {
	public static net.twisterrob.java.model.Location gridRef2LatLon(int easting, int northing) {
		Location OSGB36 = MoveableTypeGridRefLocationConverter.f(easting, northing);
		Location WGS84 = MoveableTypeGridRefLocationConverter.convertOSGB36toWGS84(OSGB36);
		return WGS84;
	}
}
