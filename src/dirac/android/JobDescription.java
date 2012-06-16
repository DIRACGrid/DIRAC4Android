package dirac.android;

public class JobDescription {
	private String[] parametricInputData;
	private String executable;
	private String softwareDistModule;
	private String jobName;
	private String priority;
	private JobRequirements jobRequirements;		
	private String arguments;
	private String ancestorDepth;
	private String softwarePackages;
	private String inputDataModule;
	private String virtualOrganization;
	private String logLevel;
	private String[] inputSandbox;
	private String ownerName;
	private String[] outputSandbox;
	private String jobType;
	private String systemConfig;
	private String gridEnv;
	private String DIRACSetup;
	private String stdError;
	private String[] parametricInputSandbox;
	private String CPUTime;
	private String ownerDN;
	private String jobGroup;
	private String stdOutput;
	private String jobID;	
	private String origin;
	private String[] site;
	private String ownerGroup;
	private String owner;
	private String maxCPUTime;
	private String[] InputData;

	public String getOwnerDN() {
		return ownerDN;
	}
	public void setOwnerDN(String ownerDN) {
		this.ownerDN = ownerDN;
	}


}
