package edu.jhuapl.sbmt.core.client;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.jhuapl.saavtk.colormap.Colormaps;
import edu.jhuapl.saavtk.util.Configuration;
import edu.jhuapl.saavtk.util.Configuration.ReleaseType;

public enum Mission
{
	APL_INTERNAL_NIGHTLY("b1bc7ec", false),
	APL_INTERNAL("b1bc7ed", false),
	PUBLIC_RELEASE("3ee38f0", true),
	TEST_APL_INTERNAL("fb404a7", false),
	TEST_PUBLIC_RELEASE("a1a32b4", true),
	HAYABUSA2_DEV("133314b", false),
	// HAYABUSA2_STAGE("244425c", false),
	HAYABUSA2_DEPLOY("355536d", false),
	OSIRIS_REX("7cd84586", false),
	// OSIRIS_REX_STAGE("7cd84587", false),
	OSIRIS_REX_DEPLOY("7cd84588", false),
	OSIRIS_REX_TEST("h887aa63", false),
	OSIRIS_REX_MIRROR_DEPLOY("7cd84589", false),
	NH_DEPLOY("8ff86312", false),
	DART_DEV("9da75292", false),
	DART_DEPLOY("9da75293", false),
	DART_TEST("8f449edc", false),
	DART_STAGE("afac11cb", false),
	STAGE_APL_INTERNAL("f7e441b", false),
	STAGE_PUBLIC_RELEASE("8cc8e12", true),
	MEGANE_DEV("9da85292", false),
	MEGANE_DEPLOY("9da85293", false),
	MEGANE_TEST("8f549edc", false),
	MEGANE_STAGE("afad11cb", false),
    LUCY_DEPLOY("f004bc5d", false),
    LUCY_TEST("3d4ff830", false),
    LUCY_STAGE("77ffedc1", false),
	;

	private final String hashedName;
	private String launchImageFilename;
	private String appName;
	private String cacheVersion;
	private String appTitle;
	private String rootURL;
	private String defaultColormapName;

	private final boolean publishedDataOnly;

	// DO NOT change anything about this without also confirming the script
	// set-released-mission.sh still works correctly!
	// This field is used during the build process to "hard-wire" a release to point
	// to a specific server.
	// private static final Mission RELEASED_MISSION = Mission.APL_INTERNAL; //for
	// generating the allBodies.json metadata file, for example
	private static final Mission RELEASED_MISSION = null; // normal ops

	private static Mission mission = RELEASED_MISSION;
	private static boolean missionConfigured = false;
	
	public static Date compileDate = null;
	public static String versionString = "\n";

	Mission(String hashedName, boolean publishedDataOnly)
	{
		this.hashedName = hashedName;
		this.publishedDataOnly = publishedDataOnly;
	}

	public String getHashedName()
	{
		return hashedName;
	}

	public boolean isPublishedDataOnly()
	{
		return publishedDataOnly;
	}

	public static Mission getMissionForName(String name)
	{
		for (Mission msn : values())
		{
			if (name.equals(msn.hashedName))
				return msn;
		}
		return null;
	}

	public static Mission getMission()
	{
		if (mission == null)
		{
			// Note that System.getProperty is inconsistent with regard to whether it
			// includes quote marks.
			// To be sure the mission identifier is processed consistently, exclude all
			// non-word characters.
			String missionIdentifier = System.getProperty("edu.jhuapl.sbmt.mission").replaceAll("\\W+", "");
			if (missionIdentifier == null)
			{
				throw new IllegalArgumentException("Mission was not specified at build time or run time");
			}
			try
			{
				// First see if provided mission identifier matches the enumeration
				// name.
				mission = Mission.valueOf(missionIdentifier);
			}
			catch (IllegalArgumentException e)
			{
				// No mission identifier with that natural enumeration name,
				// so see if instead this is a hashed mission identifier.
				for (Mission each : Mission.values())
				{
					if (each.getHashedName().equalsIgnoreCase(missionIdentifier))
					{
						mission = each;
						break;
					}
				}
				if (mission == null)
				{
					throw new IllegalArgumentException(
							"Invalid mission identifier specified at run time: " + missionIdentifier, e);
				}
			}
		}

		return mission;
	}
	
	private static void generateCompileDateAndVersionString(Object sbmtClass) {


		if (compileDate != null && !versionString.equals("\n")) return;
        try
        {
        	Class<?> classToLoad = sbmtClass.getClass();
        	ClassLoader classLoader = classToLoad.getClassLoader();
        	String classNameToLoad = sbmtClass.getClass().getCanonicalName().replace('.', '/').substring(0, sbmtClass.getClass().getCanonicalName().length());
        	URL classURL = classLoader.getResource(classNameToLoad + ".class");
        	File file = new File(classURL.toURI());
            compileDate = new Date(file.lastModified());
        }
        catch (@SuppressWarnings("unused") Exception e)
        {
            try {
                String rn = sbmtClass.getClass().getName().replace('.', '/') + ".class";
                JarURLConnection j = (JarURLConnection) ClassLoader.getSystemResource(rn).openConnection();
                long time =  j.getJarFile().getEntry("META-INF/MANIFEST.MF").getTime();
                compileDate = new Date(time);
            } catch (@SuppressWarnings("unused") Exception e1) {
            }
        }

        try
        {
            InputStream is = sbmtClass.getClass().getResourceAsStream("/edu/jhuapl/sbmt/data/release.version");
            byte[] data = new byte[256];
            is.read(data, 0, data.length);
            versionString = new String(data).trim();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
	    	System.out.println("exception = " + e.toString());
        }
	}
	
//	public static Mission configureMission(String rootUrl)
//	{
//		String rootUrl = "https://sbmt.jhuapl.edu/sbmt/prod";
//
//        switch (mission)
//        {
//        case DART_DEPLOY:
//            // TODO: set rootUrl when this is determined.
//            break;
//        case STAGE_APL_INTERNAL:
//        case STAGE_PUBLIC_RELEASE:
//        case DART_STAGE:
//            rootUrl = "https://sbmt.jhuapl.edu/internal/multi-mission/stage";
//            break;
//        case TEST_APL_INTERNAL:
//        case TEST_PUBLIC_RELEASE:
//        case DART_TEST:
//        case OSIRIS_REX_TEST:
//            rootUrl = "http://sbmt-web.jhuapl.edu/internal/multi-mission/test";
//            break;
//        case HAYABUSA2_DEPLOY:
//            rootUrl = "http://hyb2sbmt.u-aizu.ac.jp/sbmt";
//            break;
//        case OSIRIS_REX_DEPLOY:
//            rootUrl = "https://uasbmt.lpl.arizona.edu/sbmt";
//            break;
//        default:
//            break;
//        }
//		
//		Configuration.setRootURL(rootUrl);
//		return configureMission();
//	}

	public static Mission configureMission()
	{
		if (missionConfigured)
		{
			return mission;
		}
		Mission mission = getMission();
		generateCompileDateAndVersionString(mission);
		
		String rootUrl = "https://sbmt.jhuapl.edu/sbmt/prod";
		switch (mission)
	    {
        case DART_DEPLOY:
            // TODO: set rootUrl when this is determined.
            break;
        case STAGE_APL_INTERNAL:
        case STAGE_PUBLIC_RELEASE:
        case DART_STAGE:
        case LUCY_STAGE:
            rootUrl = "https://sbmt.jhuapl.edu/internal/multi-mission/stage";
            break;
        case TEST_APL_INTERNAL:
        case TEST_PUBLIC_RELEASE:
        case DART_TEST:
        case OSIRIS_REX_TEST:
        case LUCY_TEST:
            rootUrl = "http://sbmt-web.jhuapl.edu/internal/multi-mission/test";
            break;
        case HAYABUSA2_DEPLOY:
            rootUrl = "http://hyb2sbmt.u-aizu.ac.jp/sbmt";
            break;
        case OSIRIS_REX_DEPLOY:
            rootUrl = "https://uasbmt.lpl.arizona.edu/sbmt";
            break;
        default:
            break;
        }
		
		Configuration.setRootURL(rootUrl);
    		
		switch (mission)
		{
		case APL_INTERNAL_NIGHTLY:
			Configuration.setAppName("sbmt-internal-nightly");
			Configuration.setCacheVersion("2");
			Configuration.setAppTitle("SBMT");
			Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
			break;
		case APL_INTERNAL:
			Configuration.setAppName("sbmt-apl");
	        Configuration.setCacheVersion("2");
	        Configuration.setAppTitle("SBMT");
	        break;
		case PUBLIC_RELEASE:
			Configuration.setAppName("sbmt");
			Configuration.setCacheVersion("2");
			Configuration.setAppTitle("SBMT");
			break;
		case STAGE_APL_INTERNAL:
			Configuration.setAppName("sbmt-stage-apl");
            Configuration.setCacheVersion("2");
            Configuration.setAppTitle("SBMT");
            break;
		case STAGE_PUBLIC_RELEASE:
			Configuration.setAppName("sbmt-stage");
			Configuration.setCacheVersion("2");
			Configuration.setAppTitle("SBMT");
			break;
		case TEST_APL_INTERNAL:
			Configuration.setAppName("sbmt-test-apl");
            Configuration.setCacheVersion("2");
            Configuration.setAppTitle("SBMT");
            break;
		case TEST_PUBLIC_RELEASE:
			Configuration.setAppName("sbmt-test");
			Configuration.setCacheVersion("2");
			Configuration.setAppTitle("SBMT");
			break;
		case HAYABUSA2_DEV:
			Configuration.setAppName("sbmthyb2-dev");
			Configuration.setCacheVersion("");
			Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
			Configuration.setAppTitle("SBMT/Hayabusa2-Dev");
			break;
		// case HAYABUSA2_STAGE:
		// Configuration.setRootURL("http://hyb2sbmt.jhuapl.edu/sbmt");
		// Configuration.setAppName("sbmthyb2-stage");
		// Configuration.setCacheVersion("");
		// Configuration.setAppTitle("SBMT/Hayabusa2-Stage");
		// break;
		case HAYABUSA2_DEPLOY:
			Configuration.setAppName("sbmthyb2");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/Hayabusa2");
			break;
		case OSIRIS_REX:
			Configuration.setAppName("sbmt1orex-dev");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/OSIRIS REx-Dev");
			Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
        case OSIRIS_REX_TEST:
            Configuration.setAppName("sbmt1orex-test");
            Configuration.setCacheVersion("");
            Configuration.setAppTitle("SBMT/OSIRIS REx-Test" /*+ versionString + " (" + compileDateString + ")"*/);
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
            break;
		// case OSIRIS_REX_STAGE:
		// Configuration.setRootURL("http://orexsbmt.jhuapl.edu/sbmt");
		// Configuration.setAppName("sbmt1orex-stage");
		// Configuration.setCacheVersion("");
		// Configuration.setAppTitle("SBMT/OSIRIS REx-Stage");
		// break;
		case OSIRIS_REX_MIRROR_DEPLOY:
			// Configuration.setRootURL("http://sbmt.jhuapl.edu/sbmt");
			Configuration.setAppName("sbmt1orex-mirror");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/OSIRIS REx APL Mirror");
			Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
		case OSIRIS_REX_DEPLOY:
			Configuration.setAppName("sbmt1orex");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/OSIRIS REx");
			Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
		case NH_DEPLOY:
			Configuration.setAppName("sbmtnh");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/New Horizons");
			break;
		case DART_DEV:
			Configuration.setAppName("sbmt1dart-dev");
			Configuration.setCacheVersion("");
			Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
			Configuration.setAppTitle("SBMT/DART (Development Version)" /*+ "(" + compileDateString + ")"*/);
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
		case DART_DEPLOY:
			Configuration.setAppName("sbmt1dart");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/DART" /*+ versionString + " (" + compileDateString + ")"*/);
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
        case DART_STAGE:
            Configuration.setAppName("sbmt1dart-stage");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/DART (Stage Version)" /*+ "(" + compileDateString + ")"*/);
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
            break;
        case DART_TEST:
            Configuration.setAppName("sbmt1dart-test");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/DART (Test Version)" /*+ "(" + compileDateString + ")"*/);
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
            break;
        case MEGANE_DEV:
			Configuration.setAppName("sbmt1megane-dev");
			Configuration.setCacheVersion("");
			Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
			Configuration.setAppTitle("SBMT/MEGANE (Development Version)");
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
		case MEGANE_DEPLOY:
			Configuration.setAppName("sbmt1megane");
			Configuration.setCacheVersion("");
			Configuration.setAppTitle("SBMT/MEGANE");
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
			break;
        case MEGANE_STAGE:
            Configuration.setAppName("sbmt1megane-stage");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/MEGANE (Stage Version)");
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
            break;
        case MEGANE_TEST:
            Configuration.setAppName("sbmt1megane-test");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/MEGANE (Test Version)" );
            Colormaps.setDefaultColormapName("Spectral_lowBlue");
            break;
        case LUCY_DEPLOY:
            Configuration.setAppName("sbmt1lucy");
            Configuration.setCacheVersion("");
            Configuration.setAppTitle("SBMT/LUCY");
            break;
        case LUCY_STAGE:
            Configuration.setAppName("sbmt1lucy-stage");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/LUCY (Stage Version)");
            break;
        case LUCY_TEST:
            Configuration.setAppName("sbmt1lucy-test");
            Configuration.setCacheVersion("");
            Configuration.setReleaseType(ReleaseType.DEVELOPMENT);
            Configuration.setAppTitle("SBMT/LUCY (Test Version)" );
            break;
		default:
			throw new AssertionError();
		}
		missionConfigured = true;
		return mission;
	}
	
	public static String getDisplaySplashName(Mission mission)
	{
		switch (mission)
        {
        case APL_INTERNAL_NIGHTLY:
        case APL_INTERNAL:
        case PUBLIC_RELEASE:
        case STAGE_APL_INTERNAL:
        case STAGE_PUBLIC_RELEASE:
        case TEST_APL_INTERNAL:
        case TEST_PUBLIC_RELEASE:
        case NH_DEPLOY:
        case DART_DEV:
        case DART_DEPLOY:
        case DART_STAGE:
        case DART_TEST:
        case MEGANE_DEV:
        case MEGANE_DEPLOY:
        case MEGANE_STAGE:
        case MEGANE_TEST:
        case LUCY_DEPLOY:
        case LUCY_STAGE:
        case LUCY_TEST:
            return "splashLogo.png";
        case HAYABUSA2_DEV:
            return "splashLogoHb2Dev.png";
//        case HAYABUSA2_STAGE:
//            splash = new SbmtSplash("resources", "splashLogoHb2Stage.png");
//            break;
        case HAYABUSA2_DEPLOY:
            return "splashLogoHb2.png";
        case OSIRIS_REX:
        case OSIRIS_REX_TEST:
        case OSIRIS_REX_DEPLOY:
        case OSIRIS_REX_MIRROR_DEPLOY:
//        case OSIRIS_REX_STAGE:
            return "splashLogoOrex.png";
        default:
            throw new AssertionError("Unhandled splash screen case for launch configuration " + mission);
        }
	}

	public String getLaunchImageFilename()
	{
		return launchImageFilename;
	}

	public String getAppName()
	{
		return appName;
	}

	public String getCacheVersion()
	{
		return cacheVersion;
	}

	public String getAppTitle()
	{
		return appTitle;
	}

	public String getRootURL()
	{
		return rootURL;
	}

	public String getDefaultColormapName()
	{
		return defaultColormapName;
	}

	public static Mission getReleasedMission()
	{
		return RELEASED_MISSION;
	}

	public static boolean isMissionConfigured()
	{
		return missionConfigured;
	}
}