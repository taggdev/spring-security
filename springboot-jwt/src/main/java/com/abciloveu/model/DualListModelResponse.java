package com.abciloveu.model;

import java.util.Objects;

public class DualListModelResponse implements Comparable<DualListModelResponse> {
	
    private Long value;
    private String label;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
	public int compareTo(DualListModelResponse o) {
		return this.label.compareTo(o.label);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (!(obj instanceof DualListModelResponse)) {
			return false;
		}
		
		DualListModelResponse other = (DualListModelResponse) obj;
		return Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append("[value=")
				.append(value).append(", label=").append(label).append("]")
				.toString();
	}
	
	

    
}
