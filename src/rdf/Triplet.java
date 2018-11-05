package rdf;

public class Triplet {
	
	public String s, p, o;
	public Integer indexS, indexP, indexO, select; 
	
	public Triplet(String s, String p, String o) {
		this.s = s;
		this.p = p;
		this.o = o;
	}
	
	public void bind(Integer p, Integer o) {
		indexP = p;
		indexO = o;
	}
	
	public String getSubjectString() {
		return s;
	}
	
	public Integer getSubjectIndex() {
		return indexS;
	}
	
	public String getPredicateString() {
		return p;
	}
	
	public Integer getPredicateIndex() {
		return indexP;
	}
	
	public String getObjectString() {
		return o;
	}
	
	public Integer getObjectIndex() {
		return indexO;
	}
	
	public String toString(){
		return s + " " + p + " " + o;
	}

	public int getSelect() {
        return select;
    }

    void setSelect(int s) {
        this.select = s;
    }

	
}
