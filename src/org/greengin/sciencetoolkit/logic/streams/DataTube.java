package org.greengin.sciencetoolkit.logic.streams;

import java.util.Vector;

public class DataTube {

	DataOutput origin;
	Vector<DataInputOutput> filters;
	DataInput end;
	
	public DataTube(DataOutput origin) {
		this.origin = origin;
		this.filters = new Vector<DataInputOutput>();
		this.end = null;
	}
	
	public void append(DataInputOutput filter) {
		this.filters.add(filter);
	}
	
	public void setEnd(DataInput end) {
		this.end = end;
	}
	
	public void attach() {
		DataOutput last = origin;
		for (DataInputOutput filter : filters) {
			last.addInput(filter);
			last = filter;
		}
		last.addInput(end);		
	}
	
	public void detach() {
		DataOutput last = origin;
		for (DataInputOutput filter : filters) {
			last.removeInput(filter);
			last = filter;
		}
		last.removeInput(end);
	}
	

}
