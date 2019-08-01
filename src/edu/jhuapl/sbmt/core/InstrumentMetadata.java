package edu.jhuapl.sbmt.core;

import java.util.ArrayList;
import java.util.List;

import edu.jhuapl.sbmt.spectrum.model.core.interfaces.SearchSpec;


public interface InstrumentMetadata<S extends SearchSpec>
{

    void setSpecs(ArrayList<S> specs);

    List<S> getSpecs();

    void addSearchSpecs(List<S> specs);

    void addSearchSpec(S spec);

    String getInstrumentName();

    void setInstrumentName(String instrumentName);

    String getQueryType();

    void setQueryType(String queryType);

    String toString();

}
