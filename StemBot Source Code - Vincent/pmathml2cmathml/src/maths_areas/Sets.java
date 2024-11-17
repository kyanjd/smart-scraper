package maths_areas;

import org.w3c.dom.Document;

import maths_areas.sets.CommonSets;
import maths_areas.sets.ElementOf;
import maths_areas.sets.Intervals;
import maths_areas.sets.Lists;
import maths_areas.sets.SimpleSets;

public class Sets{
	
	public Document doc; 
	
	public Sets(Document doc) {
		this.doc = doc;
	}

	/*
	 * Convert all presentation that maps onto this area of maths
	 */
	@SuppressWarnings("unused")
	public void convert() {
		//TODO sort order:
		Intervals in = new Intervals(doc);
		Lists lsts = new Lists(doc);
		SimpleSets ss = new SimpleSets(doc);
		ElementOf eo = new ElementOf(doc);
		CommonSets cs = new CommonSets(doc);

	}


}
