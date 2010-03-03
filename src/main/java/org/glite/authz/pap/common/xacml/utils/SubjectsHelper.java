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

package org.glite.authz.pap.common.xacml.utils;

import java.util.List;

import org.opensaml.xacml.policy.SubjectType;
import org.opensaml.xacml.policy.SubjectsType;

public class SubjectsHelper extends XMLObjectHelper<SubjectsType> {

    private static final javax.xml.namespace.QName elementQName = SubjectsType.DEFAULT_ELEMENT_NAME;
    private static final SubjectsHelper instance = new SubjectsHelper();

    private SubjectsHelper() {}

    public static SubjectsType build() {
        return (SubjectsType) builderFactory.getBuilder(elementQName).buildObject(elementQName);
    }

    public static SubjectsType build(List<SubjectType> subjectList) {
        
        if (subjectList.isEmpty()) {
            return null;
        }
        
        SubjectsType subjects = build();
        
        for (SubjectType subject : subjectList) {
            subjects.getSubjects().add(subject);
        }
        
        return subjects;
    }

    public static SubjectsType build(SubjectType subject) {
        
        if (subject == null) {
            return null;
        }
        
        SubjectsType subjects = build();
        subjects.getSubjects().add(subject);
        
        return subjects;
    }

    public static SubjectsHelper getInstance() {
        return instance;
    }

}
