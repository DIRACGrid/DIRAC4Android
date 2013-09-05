package dirac.android;

public class Status {

    private String name;
    private String number;


    public  static String[] PossibleStatus = {
    					  "Running",
					      "Waiting",
					      "Completed",
					      "Done",
					      "Failed",
					      "Matched",
					      "Received",
					      "Checking",
					      "Staging",
					      "Stalled",
					      "Killed",
					      "Deleted"};

    public static  int[] ColorStatus = {
    	            R.color.Running,
					R.color.Waiting,
					R.color.Completed,
					R.color.Done,
					R.color.Failed,
					R.color.Matched,
					R.color.Received,
					R.color.Checking,
					R.color.Staging,
					R.color.Stalled,
					R.color.Killed,
					R.color.Deleted};

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
