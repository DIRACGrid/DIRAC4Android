package dirac.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Job  implements Parcelable  {
	public Integer id;
	public String state;
	public String name;
	public String site;
	public String time;
	private int mData;

	public Job()
	{
		// TODO Auto-generated constructor stub
	}

	public Job(Integer id, String state, String name, String site, String time)
	{
		this.id = id;
		this.state = state;
		this.name = name;
		this.time = time;
		this.site = site;
	}


	public String JobState(Integer id)
	{
		return this.state;
	}


	@Override
	public String toString()
	{
		return this.id.toString();
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		arg0.writeString(state);
		arg0.writeString(name);
		arg0.writeString(site);
		arg0.writeString(time);

	}

	// this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
	public static final Parcelable.Creator<Job> CREATOR = new Parcelable.Creator<Job>() {
		public Job createFromParcel(Parcel in) {
			return new Job(in);
		}

		public Job[] newArray(int size) {
			return new Job[size];
		}
	};

	// example constructor that takes a Parcel and gives you an object populated with it's values
	private Job(Parcel in) {


		id=in.readInt();
		state=in.readString();
		name=in.readString();
		site=in.readString();
		time=in.readString();

	}






}
