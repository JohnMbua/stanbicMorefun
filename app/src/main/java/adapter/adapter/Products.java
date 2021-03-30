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

public class Products {

	private String name;
    private String head;
	private String desc;
    private int img;



	public Products(String name, String desc,String head,int img) {
		this.name = name;
this.img = img;
         this.head = head;
		this.desc = desc;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
    public String getHead() {
        return head;
    }
    public void setHead(String desc) {
        this.head = desc;
    }

    public int getImg() {
        return img;
    }
    public void setImg(int desc) {
        this.img = desc;
    }
	
	
}
