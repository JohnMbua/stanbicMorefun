package adapter.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class ImagesBean implements Parcelable {

	private int id;

	private String imageUrl;

	public ImagesBean() {
		super();
	}

	private ImagesBean(Parcel in) {
		super();
		this.id = in.readInt();

		this.imageUrl = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(getId());

		parcel.writeString(getImageUrl());
	}

	public static final Creator<ImagesBean> CREATOR = new Creator<ImagesBean>() {
		public ImagesBean createFromParcel(Parcel in) {
			return new ImagesBean(in);
		}

		public ImagesBean[] newArray(int size) {
			return new ImagesBean[size];
		}
	};

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public static Creator<ImagesBean> getCreator() {
		return CREATOR;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImagesBean other = (ImagesBean) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", imageUrl="
				+ imageUrl + "]";
	}
}
