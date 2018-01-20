/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Song;
import com.emacours.planner.model.Studio;
import java.util.Arrays;

/**
 *
 * @author cyann
 */
public class Planning {

    private final Song[] planning;
    private final DataModel model;

    public Planning(Song[] planning, DataModel model) {
        this.planning = planning;
        this.model = model;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + Arrays.deepHashCode(this.planning);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Planning other = (Planning) obj;
        if (!Arrays.deepEquals(this.planning, other.planning)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < planning.length; i++) {
            int s = i % model.getStudioDomainSize();
            int t = i / model.getStudioDomainSize();

            if (s == 0 && i != 0) {
                sb.append('\n');
            } 
            
            if (s == 0) {
                sb.append(model.getSlots().get(t).getName());
                sb.append("\t| ");
            } else {
                sb.append(", ");
            }
            
            sb.append(model.getStudios().get(s).getName());
            sb.append("-");
            sb.append(planning[i]);
        }

        return sb.toString();
    }

}
