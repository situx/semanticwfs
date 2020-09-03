package de.hsmainz.cs.semgis.wfs.util;

import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import org.apache.sis.geometry.Envelope2D;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.OctagonalEnvelope;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.proj4j.CRSFactory;
import org.locationtech.proj4j.CoordinateReferenceSystem;
import org.locationtech.proj4j.CoordinateTransform;
import org.locationtech.proj4j.CoordinateTransformFactory;
import org.locationtech.proj4j.ProjCoordinate;

public class ReprojectionUtils {

	static CRSFactory csFactory = new CRSFactory();
	
	static CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
	
	static GeometryFactory fac=new GeometryFactory();
	
	public static String crsURIToEPSG(String uri) {
		if(uri.startsWith("http")) {
			uri=uri.replace(">","");
			return "EPSG:"+uri.substring(uri.lastIndexOf('/')+1);
		}
		return uri;
	}
	
	public static Geometry reproject(Geometry geom,String sourceCRS,String targetCRS) {
		String src=crsURIToEPSG(sourceCRS);
		String target=crsURIToEPSG(targetCRS);
		if(src.equals(target)) {
			return geom;
		}
		CoordinateReferenceSystem crs1 = csFactory.createFromName(src);
        CoordinateReferenceSystem crs2 = csFactory.createFromName(target);
        CoordinateTransform trans = ctFactory.createTransform(crs1, crs2);
        Coordinate[] oldcoords=geom.getCoordinates();
        Coordinate[] newcoords=new Coordinate[oldcoords.length];
        int i=0;
		for(Coordinate cur:oldcoords) {
			 ProjCoordinate p1 = new ProjCoordinate();
		     ProjCoordinate p2 = new ProjCoordinate();
		     p1.x = cur.x;
		     p1.y = cur.y;
		     trans.transform(p1, p2);
		     newcoords[i]=new Coordinate(p2.x,p2.y);
		}
		return ReprojectionUtils.createGeometry(newcoords, geom.getGeometryType(), geom.getSRID());
	}
	
public static Geometry toGeometry(final org.opengis.geometry.Envelope envelope) {
		
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getLowerCorner().getDirectPosition().getCoordinate()[0], envelope.getLowerCorner().getDirectPosition().getCoordinate()[1]),
                    new Coordinate(envelope.getUpperCorner().getDirectPosition().getCoordinate()[0], envelope.getLowerCorner().getDirectPosition().getCoordinate()[1]),
                    new Coordinate(envelope.getUpperCorner().getDirectPosition().getCoordinate()[0], envelope.getUpperCorner().getDirectPosition().getCoordinate()[1]),
                    new Coordinate(envelope.getLowerCorner().getDirectPosition().getCoordinate()[0], envelope.getLowerCorner().getDirectPosition().getCoordinate()[1]),
                    new Coordinate(envelope.getLowerCorner().getDirectPosition().getCoordinate()[0], envelope.getLowerCorner().getDirectPosition().getCoordinate()[1])
                }), null);
    }
	
	public static Geometry toGeometry(final Envelope envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }
	
	public static Geometry toGeometry(final Envelope2D envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }
	
	public static Geometry toGeometry(final OctagonalEnvelope envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }
	

	
	/*public static Geometry toGeometry(final BoundingBox envelope) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPolygon(gf.createLinearRing(
                new Coordinate[]{
                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
                    new Coordinate(envelope.getMinX(), envelope.getMinY())
                }), null);
    }*/
	
	 public static Geometry toGeometry(final Rectangle envelope) {
	        GeometryFactory gf = new GeometryFactory();
	        return gf.createPolygon(gf.createLinearRing(
	                new Coordinate[]{
	                    new Coordinate(envelope.getMinX(), envelope.getMinY()),
	                    new Coordinate(envelope.getMaxX(), envelope.getMinY()),
	                    new Coordinate(envelope.getMaxX(), envelope.getMaxY()),
	                    new Coordinate(envelope.getMinX(), envelope.getMaxY()),
	                    new Coordinate(envelope.getMinX(), envelope.getMinY())
	                }), null);
	    }
	
	 
		public static Geometry createGeometry(Coordinate[] coordinates,String geomtype,Integer srid) {
			GeometryFactory fac=new GeometryFactory();
			Geometry geom;
			switch(geomtype) {
			case "Point":
				geom= fac.createPoint(coordinates[0]);
				geom.setSRID(srid);
				return geom;
			case "MultiPoint":
				geom=fac.createMultiPointFromCoords(coordinates);
				geom.setSRID(srid);
				return geom;
			case "LineString":
				geom= fac.createLineString(coordinates);
				geom.setSRID(srid);
				return geom;
			case "Polygon":
				geom= fac.createPolygon(coordinates);
				geom.setSRID(srid);
				return geom;
			case "MultiLineString":
				List<LineString> list=new LinkedList<LineString>();
				list.add(fac.createLineString(coordinates));
				geom= fac.createMultiLineString(list.toArray(new LineString[0]));
				geom.setSRID(srid);
				return geom;
			case "MultiPolygon":
				List<Polygon> plist=new LinkedList<Polygon>();
				plist.add(fac.createPolygon(coordinates));
				geom= fac.createMultiPolygon(plist.toArray(new Polygon[0]));
				geom.setSRID(srid);
				return geom;
			default:
				return null;
			}
		}

		public static Geometry createGeometryCollection(List<Geometry> geometries,String geomtype,Integer srid) {
			return createGeometryCollection(geometries.toArray(new Geometry[0]), geomtype, srid);		
		}
		
		public static Geometry createGeometryCollection(Geometry[] geometries,String geomtype,Integer srid) {
			GeometryFactory fac=new GeometryFactory();
			return fac.createGeometryCollection(geometries);			
		}
		
		public static Geometry createGeometry(List<Coordinate> coordarray,String geomtype,Integer srid) {
			return createGeometry(coordarray.toArray(new Coordinate[0]), geomtype,srid);
		}
		

	
}
