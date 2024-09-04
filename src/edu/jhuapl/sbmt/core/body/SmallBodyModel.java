package edu.jhuapl.sbmt.core.body;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.jhuapl.saavtk.config.IBodyViewConfig;
import edu.jhuapl.saavtk.model.GenericPolyhedralModel;
import edu.jhuapl.saavtk.model.LidarDataSource;
import edu.jhuapl.saavtk.util.MapUtil;
import edu.jhuapl.sbmt.core.config.ISmallBodyViewConfig;
import edu.jhuapl.sbmt.core.config.Instrument;
import vtk.vtkPolyData;
import vtk.vtkTransform;
import vtk.vtkTransformFilter;

public class SmallBodyModel extends GenericPolyhedralModel implements ISmallBodyModel
{
    private static final String[] DEFAULT_COLORING_NAMES = {
            SlopeStr, ElevStr, GravAccStr, GravPotStr
    };
    private static final String[] DEFAULT_COLORING_UNITS = {
            SlopeUnitsStr, ElevUnitsStr, GravAccUnitsStr, GravPotUnitsStr
    };
    private static final ColoringValueType DEFAULT_COLORING_VALUE_TYPE = ColoringValueType.CELLDATA;
    
    private List<LidarDataSource> lidarDataSourceL = new ArrayList<>();

	public ISmallBodyViewConfig getSmallBodyConfig()
    {
        return (ISmallBodyViewConfig)getConfig();
    }

	public String serverPath(String fileName)
    {
        return getSmallBodyConfig().serverPath(fileName);
    }

    public String serverPath(String fileName, Instrument instrument)
    {
        return getSmallBodyConfig().serverPath(fileName, instrument);
    }

    /**
     * Default constructor. Must be followed by a call to setSmallBodyPolyData.
     */
    public SmallBodyModel(String uniqueModelId)
    {
        super(uniqueModelId);
    }

    /**
     * Convenience method for initializing a SmallBodyModel with just a vtkPolyData.
     * @param polyData
     */
    public SmallBodyModel(String uniqueModelId, vtkPolyData polyData)
    {
        super(uniqueModelId, polyData);
    }

    public SmallBodyModel(IBodyViewConfig config)
    {
        super(config);
    }

    protected SmallBodyModel(
            IBodyViewConfig config,
            String[] modelNames,
            String[] coloringFiles,
            String[] coloringNames,
            String[] coloringUnits,
            boolean[] coloringHasNulls,
            ColoringValueType coloringValueType,
            boolean lowestResolutionModelStoredInResource)
    {
        this(config, modelNames, config.getShapeModelFileNames(), coloringFiles, coloringNames, coloringUnits, coloringHasNulls, coloringValueType, lowestResolutionModelStoredInResource);
    }

    /**
     * Note that name is used to name this small body model as a whole including all
     * resolution levels whereas modelNames is an array of names that is specific
     * for each resolution level.
     */
    private SmallBodyModel(
            IBodyViewConfig config,
            String[] modelNames,
            String[] modelFiles,
            String[] coloringFiles,
            String[] coloringNames,
            String[] coloringUnits,
            boolean[] coloringHasNulls,
            ColoringValueType coloringValueType,
            boolean lowestResolutionModelStoredInResource)
    {
        super(config, modelNames, modelFiles, coloringFiles, coloringNames, coloringUnits, coloringHasNulls, coloringValueType, lowestResolutionModelStoredInResource);
    	if (!getConfig().isCustomTemporary())
        {
            loadCustomLidarDataSource();
        }
    }

    protected void initializeConfigParameters(
            String[] imageMapNames,
            boolean lowestResolutionModelStoredInResource)
    {
        ISmallBodyViewConfig config = getSmallBodyConfig();
        String [] modelFiles = config.getShapeModelFileNames();

        initializeConfigParameters(
                modelFiles,
                imageMapNames,
                lowestResolutionModelStoredInResource);
    }

    protected void initializeConfigParameters(
            String[] modelFiles,
            String[] imageMapNames,
            boolean lowestResolutionModelStoredInResource)
    {
        ISmallBodyViewConfig config = getSmallBodyConfig();
        final String[] coloringFiles = {
                serverPath("coloring/Slope"),
                serverPath("coloring/Elevation"),
                serverPath("coloring/GravitationalAcceleration"),
                serverPath("coloring/GravitationalPotential")
        };
        final boolean[] coloringHasNulls = null;
        initializeConfigParameters(
                modelFiles,
                config.hasColoringData() ? coloringFiles : null,
                config.hasColoringData() ? DEFAULT_COLORING_NAMES : null,
                config.hasColoringData() ? DEFAULT_COLORING_UNITS : null,
                coloringHasNulls,
                config.hasColoringData() ? DEFAULT_COLORING_VALUE_TYPE : null,
                lowestResolutionModelStoredInResource);
    }

    @Override
    public void reloadShapeModel() throws IOException
    {
    	// TODO Auto-generated method stub
    	super.reloadShapeModel();
    	transformBody(this.currentTransform);
    }

    public void transformBody(vtkTransform transform)
    {
    	this.currentTransform = transform;
		vtkTransformFilter transformFilter=new vtkTransformFilter();
		transformFilter.SetInputData(getSmallBodyPolyData());
		transformFilter.SetTransform(transform);
		transformFilter.Update();

    	vtkPolyData polydata = transformFilter.GetPolyDataOutput();
    	setSmallBodyPolyDataAtPosition(polydata);
    }
    
    public void loadCustomLidarDataSource()
    {
        lidarDataSourceL = new ArrayList<>();

        String configFilename = getConfigFilename();

        if (!(new File(configFilename).exists()))
            return;

        MapUtil configMap = new MapUtil(configFilename);

        convertOldConfigFormatToNewVersion(configMap);

		if (configMap.containsKey(GenericPolyhedralModel.LIDAR_DATASOURCE_NAMES)
				&& configMap.containsKey(GenericPolyhedralModel.LIDAR_DATASOURCE_NAMES))
        {
            String[] nameArr = configMap.get(GenericPolyhedralModel.LIDAR_DATASOURCE_NAMES).split(",", -1);
            String[] pathArr = configMap.get(GenericPolyhedralModel.LIDAR_DATASOURCE_PATHS).split(",", -1);

            for (int i = 0; i < nameArr.length; ++i)
            {
                LidarDataSource info = new LidarDataSource(nameArr[i], pathArr[i]);
                lidarDataSourceL.add(info);
            }
        }
    }
    
    public void addCustomLidarDataSource(LidarDataSource info) throws IOException
    {
        lidarDataSourceL.add(info);
    }

    public void setCustomLidarDataSource(int index, LidarDataSource info) throws IOException
    {
        lidarDataSourceL.set(index, info);
    }

    public void removeCustomLidarDataSource(int index) throws IOException
    {
        lidarDataSourceL.remove(index);
    }

    public List<LidarDataSource> getLidarDataSourceList()
    {
        return lidarDataSourceL;
    }

}
