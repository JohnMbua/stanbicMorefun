/*
 * Copyright (C) 2012 jfrankie (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package adapter.adapter;

public class OTBList implements  Comparable<OTBList> {

	private String benname;
    private String benmob;






	public OTBList(String benname, String benmob) {
		this.benname = benname;
this.benmob = benmob;

	}



	public String getBenName() {
		return benname;
	}
	public void setBenName(String bname) {
		this.benname = bname;
	}

	public String getBenmob() {
		return benmob;
	}
	public void setBenmob(String bmob) {
		this.benmob = bmob;
	}
	@Override
	public int compareTo(OTBList o) {
		return this.benname.compareTo(o.getBenName()); // dog name sort in ascending order
		//return o.getName().compareTo(this.name); use this line for dog name sort in descending order
	}

	@Override
	public String toString() {
		return this.benname;
	}
/*
    public String getAmo() {
        return amount;
    }
    public void setAmo(String amon) {
        this.amount = amon;
    }

    public String getAcctype() {
        return acctype;
    }
    public void setAcctype(String acct) {
        this.acctype = acct;
    }*/

	
	
}
