/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.emacours.planner.algorithm;

import com.emacours.planner.model.DataModel;
import com.emacours.planner.model.Song;
import java.util.Arrays;

/**
 *
 * @author cyann
 */
public class Planning {

    private final Song[] planning;
    private final DataModel model;

    public Song[] getPlanning() {
        return planning;
    }

    public Planning(Song[] planning, DataModel model) {
        this.planning = trim(planning, model);
        this.model = model;
    }

    private int trimIndex(Song[] planning, DataModel model) {
        int nt = model.getStudioDomainSize();
        if (nt == 0) return 0;
        int ns = planning.length / nt;

        for (int t = 0; t < ns; t++) {
            boolean slotHasData = false;
            for (int s = 0; s < nt; s++) {
                Song song = planning[t * nt + s];
                if (song != null) {
                    slotHasData = true;
                }
            }

            if (!slotHasData) {
                return (t+1) * nt;
            }
        }
        
        return planning.length;
    }

    private Song[] trim(Song[] planning, DataModel model) {
        int trimIndex = trimIndex(planning, model);
        return Arrays.copyOf(planning, trimIndex);
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
                sb.append("Slot ");
                sb.append(t);
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
