package edu.jhuapl.sbmt.core.io;

import java.util.Objects;

import edu.jhuapl.sbmt.core.config.Instrument;
import edu.jhuapl.sbmt.core.pointing.PointingSource;

import crucible.crust.metadata.api.Key;
import crucible.crust.metadata.api.Metadata;
import crucible.crust.metadata.api.MetadataManager;
import crucible.crust.metadata.api.Version;
import crucible.crust.metadata.impl.SettableMetadata;

public class DBRunInfo implements MetadataManager
{
    /**
     * Utility to join strings together with a delimiter of slash (/) to make a path
     * that complies with the server-side rules for file paths: lower case
     * alphanumerics and dashes only.
     * 
     * @param strings to join
     * @return strings joined and modified to make a compliant path
     */
    public static String createServerPath(String... strings)
    {
        return joinNonBlank("/", strings).replaceAll("[\\s-_]+", "-").toLowerCase();
    }

    /**
     * Utility to join strings together with a delimiter of underscore (_) to make
     * the prefix of a database table name that complies with the rules for table
     * names: lower case alphanumerics and underscores only.
     * 
     * @param strings to join
     * @return database table base name
     */
    public static String createTablePrefix(String... strings)
    {
        return joinNonBlank("_", strings).replaceAll("[\\W]+", "_").toLowerCase();
    }

    /**
     * Utility to return the correct flavor of the "image-list-full-path" file that
     * is used to locate images on the server. These have
     * {@link PointingSource}-specific forms, e.g., imagelist-fullpath-sum.txt.
     * 
     * @param source the pointing source
     * @return the name of the list file
     */
    public static String createListFileName(PointingSource source)
    {
        String suffix;
        switch (source)
        {
        case GASKELL:
            suffix = "sum";
            break;
        case CORRECTED:
            suffix = "sum-corrected";
            break;
        case GASKELL_UPDATED:
            suffix = "sum-updated";
            break;
        case SPICE:
            suffix = "info";
            break;
        case CORRECTED_SPICE:
            suffix = "info-corrected";
            break;
        default:
            // Won't happen.
            throw new IllegalArgumentException("No pointing file suffix for source " + source);
        }

        return "imagelist-fullpath-" + suffix + ".txt";
    }

    /**
     * Convenience factory method for creating a {@link DBRunInfo} from information
     * that includes the top of the model's image-specific area, e.g.,
     * "dart-didymos-v003/draco". This area holds pointing files, not images.
     * 
     * @param source the pointing source
     * @param instrumentId
     * @param bodyName
     * @param modelImageDir the top of the model's image-specific area
     * @param tablePrefix
     * @return the run info object
     */
    public static DBRunInfo fromModelImageDir(PointingSource source, Instrument instrumentId, String bodyName, String modelImageDir, String tablePrefix)
    {
        String pathToFileList = String.join("/", modelImageDir, createListFileName(source));
        return new DBRunInfo(source, instrumentId, bodyName, pathToFileList, tablePrefix);
    }

    public String pathToFileList;
    public String databasePrefix;
    public String remotePathToFileList;
    public String name;
    public PointingSource imageSource;
    public Instrument instrument;
    private final Key<String> nameKey = Key.of("name");
    private final Key<String> pathToFileListKey = Key.of("pathToFileList");
    private final Key<String> databasePrefixKey = Key.of("databasePrefix");
    private final Key<String> remotePathToFileListKey = Key.of("remotePathToFileListConfig");
    private final Key<String> imageSourceKey = Key.of("imageSource");
    private final Key<String> instrumentKey = Key.of("instrument");


    public DBRunInfo()
    {

    }

    public DBRunInfo(PointingSource source, Instrument instrument, String name, String pathToFileList)
    {
    	this.name = name;
        this.pathToFileList = pathToFileList;
        this.databasePrefix = "";
        this.remotePathToFileList = null;
        this.imageSource = source;
        this.instrument = instrument;
    }

    public DBRunInfo(PointingSource source, Instrument instrument, String name, String pathToFileList, String databasePrefix)
    {
    	this.name = name;
        this.pathToFileList = pathToFileList;
        this.databasePrefix = databasePrefix;
        this.remotePathToFileList = null;
        this.imageSource = source;
        this.instrument = instrument;
    }

    public DBRunInfo(PointingSource source, Instrument instrument, String name, String pathToFileList, String databasePrefix, String remotePathToFileList)
    {
    	this.name = name;
        this.pathToFileList = pathToFileList;
        this.databasePrefix = databasePrefix;
        this.remotePathToFileList = remotePathToFileList;
        this.imageSource = source;
        this.instrument = instrument;
    }

	@Override
	public Metadata store()
	{
		SettableMetadata metadata = SettableMetadata.of(Version.of(1, 0));
		metadata.put(nameKey, name);
		metadata.put(pathToFileListKey, pathToFileList);
		metadata.put(databasePrefixKey, databasePrefix);
		metadata.put(remotePathToFileListKey, remotePathToFileList);
		metadata.put(imageSourceKey, imageSource.toString());
		metadata.put(instrumentKey, instrument.toString());
		return metadata;
	}

	@Override
	public void retrieve(Metadata source)
	{
		name = source.get(nameKey);
		pathToFileList = source.get(pathToFileListKey);
		databasePrefix = source.get(databasePrefixKey);
		remotePathToFileList = source.get(remotePathToFileListKey);
		imageSource = PointingSource.valueFor(source.get(imageSourceKey));
		instrument = Instrument.valueFor(source.get(instrumentKey));
	}

	@Override
	public String toString()
	{
		return "DBRunInfo [pathToFileList=" + pathToFileList + ", databasePrefix=" + databasePrefix
				+ ", remotePathToFileList=" + remotePathToFileList + ", name=" + name + ", imageSource=" + imageSource
				+ ", instrument=" + instrument + "]";
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(databasePrefix, imageSource, instrument, name, pathToFileList, remotePathToFileList);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBRunInfo other = (DBRunInfo) obj;
		return Objects.equals(databasePrefix, other.databasePrefix) && imageSource == other.imageSource
				&& instrument == other.instrument && Objects.equals(name, other.name)
				&& Objects.equals(pathToFileList, other.pathToFileList)
				&& Objects.equals(remotePathToFileList, other.remotePathToFileList);
	}

    /**
     * Join strings using the specified delimiter between them. Skip over strings
     * that are null or contain no non-whitespace characters.
     * 
     * @param delim the delimiter
     * @param strings to join
     * @return the joined string
     */
    private static String joinNonBlank(String delim, String... strings)
    {
        boolean firstString = true;
        StringBuilder sb = new StringBuilder();
        for (String s : strings)
        {
            if (s == null || s.matches("^\\s*$"))
            {
                continue;
            }
            if (!firstString)
            {
                sb.append(delim);
            }
            else
            {
                firstString = false;
            }

            sb.append(s);
        }

        return sb.toString();
    }

}
