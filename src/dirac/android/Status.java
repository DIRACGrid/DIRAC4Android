package dirac.android;

public class Status {
	
	    private String name;
	    private String number;
	    
	    
	    public  static String[] PossibleStatus = {"Checking",
	    								   "Completed",
	    								   "Done",
	    								   "Failed",
	    								   "Killed",
	    								   "Matched",
	    								   "Received",
	    								   "Running",
	    								   "Staging",
	    								   "Stalled",
	    								   "Waiting"};
	    
	    
	    public static  int[] ColorStatus = {R.color.Checking,
	    							 R.color.Completed,
	    							 R.color.Done,
	    							 R.color.Failed,
	    							 R.color.Killed,
	    							 R.color.Matched,
	    							 R.color.Received,
	    							 R.color.Running,
	    							 R.color.Staging,
	    							 R.color.Stalled,
	    							 R.color.Waiting};

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
	    
		public int get(String num) {
	        for (int i = 0; i < PossibleStatus.length; i++) {

	                if (PossibleStatus[i].equals(num)) {
	                        return (i);
	                }
	        }
	        return (1);
	}
	    
	

}
