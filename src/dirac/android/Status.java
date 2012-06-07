package dirac.android;

public class Status {
	
	    private String name;
	    private String number;
	    public  String[] PossibleStatus = {"Checking","Completed","Done","Failed","Killed","Matched","Received","Running","Staging","Stalled","Waiting"};
	    
	    public Status(){
	        super();
	    }
	    
	    public Status(String name,String number) {
	        super();
	        this.name = name;
	        this.number = number;
	    }

	    @Override
	    public String toString() {
	        return this.name +": "+this.number;
	    }
	
	    public String name() {
	        return this.name;
	    }
	    public String number() {
	        return this.number;
	    }
	

}
