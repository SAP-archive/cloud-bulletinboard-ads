package com.sap.bulletinboard.statistics.models;

public class Statistics {
    public long id;
    public long viewCount;

    public Statistics(long id, long viewCount) {
        this.viewCount = viewCount;
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (viewCount ^ (viewCount >>> 32));
        return result;
    }
    

    @Override
    public String toString() {
        return "Statistics [id=" + id + ", viewCount=" + viewCount + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Statistics other = (Statistics) obj;
        return id == other.id && viewCount == other.viewCount;
    }
}
