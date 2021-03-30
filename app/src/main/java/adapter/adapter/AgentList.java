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

public class AgentList {

	private String agentname;
    private String agentmobno;
    private int imgid;







	public AgentList(String agentname, String agentmobno, int imgid) {
		this.agentname = agentname;
this.agentmobno = agentmobno;
        this.imgid = imgid;

	}



	public String getagentname() {
		return agentname;
	}
	public void setagentname(String accid) {
		this.agentname = accid;
	}

	public String getagentmobno() {
		return agentmobno;
	}
	public void setagentmobno(String curr) {
		this.agentmobno = curr;
	}

    public int getimgid() {
        return imgid;
    }
    public void setimgid(int amon) {
        this.imgid = amon;
    }




	
	
}
