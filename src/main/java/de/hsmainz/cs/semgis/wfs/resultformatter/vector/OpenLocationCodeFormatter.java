package de.hsmainz.cs.semgis.wfs.resultformatter.vector;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.locationtech.jts.geom.Geometry;

import com.google.openlocationcode.OpenLocationCode;

import de.hsmainz.cs.semgis.wfs.resultformatter.VectorResultFormatter;
import de.hsmainz.cs.semgis.wfs.resultstyleformatter.StyleObject;

public class OpenLocationCodeFormatter extends VectorResultFormatter {
	
	public OpenLocationCodeFormatter() {
		this.mimeType="text/olc";
		this.exposedType="text/olc";
		this.urlformat="olc";
		this.label="Open Location Code (OLC)";
		this.fileextension="olc";
		this.definition="https://github.com/google/open-location-code";
	}
	
	@Override
	public String formatter(ResultSet results, String startingElement, String featuretype, String propertytype,
			String typeColumn, Boolean onlyproperty, Boolean onlyhits, String srsName, String indvar, String epsg,
			List<String> eligiblenamespaces, List<String> noteligiblenamespaces, StyleObject mapstyle,
			Boolean alternativeFormat, Boolean invertXY, Boolean coverage,Writer out) throws XMLStreamException, IOException {
		String lastInd="",lat="",lon="";
		while(results.hasNext()) {
			QuerySolution solu=results.next();
			Iterator<String> varnames=solu.varNames();
			if(!solu.get(indvar).toString().equals(lastInd) || lastInd.isEmpty()) {
				lastQueriedElemCount++;
			}
			while(varnames.hasNext()) {
				String name=varnames.next();
				if(name.endsWith("_geom")) {
					Geometry geom=this.parseVectorLiteral(solu.get(name).toString().substring(0,solu.get(name).toString().indexOf("^^")),
							solu.get(name).toString().substring(solu.get(name).toString().indexOf("^^")+2), epsg, srsName);
					if(geom!=null) {				
						out.write(OpenLocationCode.encode(Double.valueOf(geom.getCentroid().getY()), Double.valueOf(geom.getCentroid().getX()))+System.lineSeparator());					
					}
				}else if(name.equalsIgnoreCase(indvar)){
					continue;
				}else if("lat".equalsIgnoreCase(name)){
					lat=solu.get(name).toString();
				}else if("lon".equalsIgnoreCase(name)){
					lon=solu.get(name).toString();
				}
			}
			if(!lat.isEmpty() && !lon.isEmpty()) {
				System.out.println("LatLon: "+lat+","+lon);
				if(lat.contains("^^")) {
					lat=lat.substring(0,lat.indexOf("^^"));
				}
				if(lon.contains("^^")) {
					lon=lon.substring(0,lon.indexOf("^^"));
				}
				Geometry geom=this.parseVectorLiteral("Point("+lon+" "+lat+")",WKTLiteral, epsg, srsName);
				if(geom!=null)
					out.write(OpenLocationCode.encode(Double.valueOf(lat), Double.valueOf(lon))+System.lineSeparator());
				lat="";
				lon="";
			}
			lastInd=solu.get(indvar).toString();
			if(lastQueriedElemCount%FLUSHTHRESHOLD==0)
				out.flush();
		}
		return "";
	}

}
