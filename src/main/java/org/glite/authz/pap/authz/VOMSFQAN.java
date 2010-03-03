/**
 * Copyright (c) Members of the EGEE Collaboration. 2006-2009.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.glite.authz.pap.authz;

/**
 * 
 * A VOMS FQAN principal.
 *
 */
public class VOMSFQAN extends BasePAPAdmin {
    
    /**
     * The fqan string of this {@link VOMSFQAN} principal.
     */
    String fqan;

    /**
     * Constructor.
     * 
     * @param fqan, the fqan string for this {@link VOMSFQAN} principal. 
     */
    public VOMSFQAN( String fqan ) {

        this.fqan = fqan;
    }

    /**
     * Returns the name of this {@link VOMSFQAN} principal, ie: the 
     * fqan string. Actually this method calls {@link #getFqan()}.
     * 
     * @return a string containing the voms fqan associated with this
     * {@link VOMSFQAN} principal 
     */
    public String getName() {

        return getFqan();
    }

    /**
     * Returns the fqan string for this {@link VOMSFQAN} principal.
     * @return a string containing the voms fqan associated with this
     * {@link VOMSFQAN} principal 
     */
    public String getFqan() {

        return fqan;
    }

    /**
     * Sets the fqan string for this {@link VOMSFQAN} principal.
     * @param fqan
     */
    public void setFqan( String fqan ) {

        this.fqan = fqan;
    }

    @Override
    public String toString() {

        return "[fqan]=" + getFqan();
    }

    @Override
    public boolean equals( Object obj ) {

        if ( !( obj instanceof VOMSFQAN ) )
            return false;

        VOMSFQAN other = (VOMSFQAN) obj;

        return fqan.equals( other.fqan );
    }

    @Override
    public int hashCode() {

        if ( fqan == null )
            return 1;

        return fqan.hashCode();

    }

}
