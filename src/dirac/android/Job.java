package dirac.android;

import java.util.List;


public class Job  {


	private String status;
	private String ownerGroup;
	private String jid;
	private String appStatus;
	private String 	minorStatus	;
	private String 	site;
	private String 	cpuTime	;
	private time times;
	private String priority;
	private flag flags;
	private String jobGroup;
	private String reschedules;
	private String owner;
	private String ownerDN;
	private String setup;
	private String name;
	private String changeStatusAction;


	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getOwnerGroup() {
		return ownerGroup;
	}
	public void setOwnerGroup(String ownerGroup) {
		this.ownerGroup = ownerGroup;
	}
	
	public String getJid() {
		return jid;
	}
	public void setJid(String jid) {
		this.jid = jid;
	}
	
	public String getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	
	public String getMinorStatus() {
		return minorStatus;
	}
	public void setMinorStatus(String minorStatus) {
		this.minorStatus = minorStatus;
	}
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	
	public String getCpuTime() {
		return cpuTime;
	}
	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}
	
	public time getTimes() {
	return times;
	}
	public void setTimes(time times) {
		this.times = times;
	}
	
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public flag getFlags() {
		return flags;
	}
	public void setFlags(flag flags) {
		this.flags = flags;
	}

	public String getJobGroup() {
		return jobGroup; 
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}	
	public String getReschedules() {
		return reschedules;
	}
	public void setReschedules(String reschedules) {
		this.reschedules = reschedules;
	}	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
	public String getOwnerDN() {
		return ownerDN;
	}
	public void setOwnerDN(String ownerDN) {
		this.ownerDN = ownerDN;
	}	
	public String getSetup() {
		return setup;
	}
	public void setSetup(String setup) {
		this.setup = setup;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}		
	
	public String getChangeStatusAction() {
		return changeStatusAction;
	}
	public void setChangeStatusAction(String changeStatusAction) {
		this.changeStatusAction = changeStatusAction;
	}		
	
	
}
