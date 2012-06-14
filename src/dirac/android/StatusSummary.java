package dirac.android;

public class StatusSummary {
	

	private String	Checking  = "Checking";
	private String	Completed = "Completed";
	private String  Done      = "Done";
	private String	Failed    = "Failed";
	private String	Killed    = "Killed";
	private String	Matched   = "Matched";
	private String	Received  = "Received";
	private String	Running   = "Running";
	private String	Staging   = "Staging";
	private String	Stalled   = "Stalled";
	private String	Waiting   = "Waiting";
	
	public String getStaging() {
		return Staging;
	}
	public void setStaging(String Staging) {
		this.Staging=Staging;
	}
	public String getStalled() {
		return Stalled;
	}
	public void setStalled(String Stalled) {
		this.Stalled = Stalled;
	}
	public String getWaiting() {
		return Waiting;
	}
	public void setWaiting(String Waiting) {
		this.Waiting = Waiting;
	}
	public String getFailed() {
		return Failed;
	}
	public void setFailed(String Failed) {
		this.Failed=Failed;
	}
	public String getKilled() {
		return Killed;
	}
	public void setKilled(String Killed) {
		this.Killed = Killed;
	}
	public String getMatched() {
		return Matched;
	}
	public void setMatched(String Matched) {
		this.Matched = Matched;
	}
	public String getReceived() {
		return Received;
	}
	public void setReceived(String Received) {
		this.Received = Received;
	}
	public String getCompleted() {
		return Completed;
	}
	public void setCompleted(String Completed) {
		this.Completed=Completed;
	}
	public String getDone() {
		return Done;
	}
	public void setDone(String Done) {
		this.Done = Done;
	}
	public String getRunning() {
		return Running;
	}
	public void setRunning(String Running) {
		this.Running = Running;
	}
	public String getChecking() {
		return Checking;
	}
	public void setChecking(String Checking) {
		this.Checking = Checking;
	}
	
	
	

}
