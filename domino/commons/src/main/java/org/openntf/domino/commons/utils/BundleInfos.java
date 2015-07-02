package org.openntf.domino.commons.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Provides Git information (in particular the commit id) from a property file git.properties. This property file will be generated by the
 * <a href="https://github.com/ktoso/maven-git-commit-id-plugin">git-commit-id-plugin</a>. A simple example for its usage in a
 * <code>pom.xml</code>:
 * 
 * <pre>
 *    &lt;build>
 *        ...
 *        &lt;plugins>
 *             &lt;plugin>
 *               &lt;groupId>pl.project13.maven&lt;/groupId>
 *               &lt;artifactId>git-commit-id-plugin&lt;/artifactId>
 *               &lt;version>2.1.15&lt;/version>
 *               &lt;executions>
 *                   &lt;execution>
 *                       &lt;goals>
 *                           &lt;goal>revision&lt;/goal>
 *                        &lt;/goals>
 *                   &lt;/execution>
 *               &lt;/executions>
 * 
 *               &lt;configuration>
 * 
 *                   &lt;!-- Default value is dd.MM.yyyy '@' HH:mm:ss z -->
 *                   &lt;dateFormat>yyyy-MM-dd HH:mm:ss&lt;/dateFormat>
 * 
 *                   &lt;!-- this is false by default, forces the plugin to generate the git.properties file -->
 *                   &lt;generateGitPropertiesFile>true&lt;/generateGitPropertiesFile>
 * 
 *                   &lt;!-- The path for the to be generated properties file, it's relative to ${project.basedir} -->
 *                   &lt;generateGitPropertiesFilename>src/main/resources/${project.groupId}.${project.artifactId}.git.properties&lt;/generateGitPropertiesFilename>
 * 
 *                   &lt;!-- @since 2.1.0 -->
 *                   &lt;!-- 
 *                       read up about git-describe on the in man, or it's homepage - it's a really powerful versioning helper 
 *                       and the recommended way to use git-commit-id-plugin. The configuration bellow is optional, 
 *                       by default describe will run "just like git-describe on the command line", even though it's a JGit reimplementation.
 *                   -->
 *                   &lt;gitDescribe>
 * 
 *                       &lt;!-- don't generate the describe property -->
 *                       &lt;skip>false&lt;/skip>
 * 
 *                       &lt;!-- 
 *                           if no tag was found "near" this commit, just print the commit's id instead, 
 *                           helpful when you always expect this field to be not-empty 
 *                       -->
 *                       &lt;always>false&lt;/always>
 *                       &lt;!--
 *                            how many chars should be displayed as the commit object id? 
 *                            7 is git's default, 
 *                            0 has a special meaning (see end of this README.md), 
 *                            and 40 is the maximum value here 
 *                       -->
 *                       &lt;abbrev>7&lt;/abbrev>
 * 
 *                       &lt;!-- when the build is triggered while the repo is in "dirty state", append this suffix -->
 *                       &lt;dirty>-dirty&lt;/dirty>
 * 
 *                       &lt;!-- Only consider tags matching the given pattern. This can be used to avoid leaking private tags from the repository. -->
 *                       &lt;match>*&lt;/match>
 * 
 *                       &lt;!-- 
 *                            always print using the "tag-commits_from_tag-g_commit_id-maybe_dirty" format, even if "on" a tag. 
 *                            The distance will always be 0 if you're "on" the tag. 
 *                       -->
 *                       &lt;forceLongFormat>false&lt;/forceLongFormat>
 *                   &lt;/gitDescribe>
 *               &lt;/configuration>
 * 
 *           &lt;/plugin>
 *           ...
 *      &lt;/plugins>
 *      ...           
 *  &lt;/build>
 * </pre>
 * 
 * Use it as e.g. <code>GitProperties.getInstance(getClass().getClassloader(), "org.openntf.domino.core").get...</code>
 * 
 * @author Steinsiek
 */
public class BundleInfos {

	public static BundleInfos getInstance(final Class<?> clazz) {
		String className = clazz.getName().replace(".", "/") + ".class";
		try {
			String classPath = clazz.getClassLoader().getResource(className).toString();

			String manifestPath = classPath.replace(className, "META-INF/MANIFEST.MF");
			Manifest manifest;
			InputStream stream;
			try {
				stream = new URL(manifestPath).openStream();
				manifest = new Manifest(stream);
				stream.close();
			} catch (FileNotFoundException fne) {
				// this happens during test
				System.err.println("Could not find MANIFEST.MF for " + clazz + ". " + fne.getMessage());
				manifest = new Manifest();
			}
			String gitPath = classPath.replace(className, "META-INF/git.properties");
			Properties props = new Properties();
			stream = new URL(gitPath).openStream();
			props.load(stream);
			stream.close();

			return new BundleInfos(props, manifest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BundleInfos(new Properties(), new Manifest());
	}

	public static final String BUNDLE_SYMBOLIC_NAME = "Bundle-SymbolicName";
	public static final String BUNDLE_NAME = "Bundle-Name";
	public static final String BUNDLE_CLASSPATH = "Bundle-ClassPath";
	public static final String BUNDLE_CATEGORY = "Bundle-Category";
	public static final String BUNDLE_VERSION = "Bundle-Version";
	public static final String BUNDLE_ACTIVATOR = "Bundle-Activator";
	public static final String BUNDLE_VENDOR = "Bundle-Vendor";
	public static final String BUNDLE_CONTACT = "Bundle-ContactAddress";
	public static final String BUNDLE_COPYRIGHT = "Bundle-Copyright";
	public static final String BUNDLE_DESCRIPTION = "Bundle-Description";
	public static final String BUNDLE_DOCURL = "Bundle-DocURL";
	public static final String BUNDLE_UPDATELOCATION = "Bundle-UpdateLocation";
	public static final String BUNDLE_NATIVECODE = "Bundle-NativeCode";
	public static final String BUNDLE_EXEC_ENV = "Bundle-RequiredExecutionEnvironment";
	public static final String EXPORT_PACKAGE = "Export-Package";
	public static final String IMPORT_PACKAGE = "Import-Package";
	public static final String DYNAMIC_IMPORT_PACKAGE = "DynamicImport-Package";
	public static final String BUNDLE_MANIFESTVERSION = "Bundle-ManifestVersion";
	public static final String SERVICE_COMPONENT = "Service-Component";

	private static final String _propUnknown = "(unknown or not supplied)";

	private String _gitBranch;
	private String _gitBuildHost;
	private String _gitBuildTime;
	private String _gitBuildUserEmail;
	private String _gitBuildUserName;
	private String _gitBuildVersion;
	private String _gitClosestTagCommitCount;
	private String _gitClosestTagName;
	private String _gitCommitIdAbbrev;
	private String _gitCommitIdDescribeShort;
	private String _gitCommitIdDescribe;
	private String _gitCommitId;
	private String _gitCommitMessageFull;
	private String _gitCommitMessageShort;
	private String _gitCommitTime;
	private String _gitCommitUserEmail;
	private String _gitCommitUserName;
	private String _gitDirty;
	private String _gitRemoteOriginUrl;
	private String _gitTags;

	private Manifest _manifest;

	private BundleInfos(final Properties props, final Manifest manifest) {
		_manifest = manifest;
		_gitBranch = props.getProperty("git.branch", _propUnknown);
		_gitBuildHost = props.getProperty("git.build.host", _propUnknown);
		_gitBuildTime = props.getProperty("git.build.time", _propUnknown);
		_gitBuildUserEmail = props.getProperty("git.build.user.email", _propUnknown);
		_gitBuildUserName = props.getProperty("git.build.user.name", _propUnknown);
		_gitBuildVersion = props.getProperty("git.build.version", _propUnknown);
		_gitClosestTagCommitCount = props.getProperty("git.closest.tag.commit.count", _propUnknown);
		_gitClosestTagName = props.getProperty("git.closest.tag.name", _propUnknown);
		_gitCommitIdAbbrev = props.getProperty("git.commit.id.abbrev", _propUnknown);
		_gitCommitIdDescribeShort = props.getProperty("git.commit.id.describe-short", _propUnknown);
		_gitCommitIdDescribe = props.getProperty("git.commit.id.describe", _propUnknown);
		_gitCommitId = props.getProperty("git.commit.id", _propUnknown);
		_gitCommitMessageFull = props.getProperty("git.commit.message.full", _propUnknown);
		_gitCommitMessageShort = props.getProperty("git.commit.message.short", _propUnknown);
		_gitCommitTime = props.getProperty("git.commit.time", _propUnknown);
		_gitCommitUserEmail = props.getProperty("git.commit.user.email", _propUnknown);
		_gitCommitUserName = props.getProperty("git.commit.user.name", _propUnknown);
		_gitDirty = props.getProperty("git.dirty", _propUnknown);
		_gitRemoteOriginUrl = props.getProperty("git.remote.origin.url", _propUnknown);
		_gitTags = props.getProperty("git.tags", _propUnknown);
	}

	public Manifest getManifest() {
		return _manifest;
	}

	public String getManifestAttribute(final String key) {
		if (getManifest() == null)
			return null;
		Attributes attr = getManifest().getMainAttributes();
		if (attr == null)
			return null;
		return attr.getValue(key);
	}

	public String getBundleName() {
		return getManifestAttribute(BUNDLE_NAME);
	}

	public String getBundleSymbolicName() {
		return getManifestAttribute(BUNDLE_SYMBOLIC_NAME);
	}

	public String getBundleVersion() {
		return getManifestAttribute(BUNDLE_VERSION);
	}

	public String getBranch() {
		return _gitBranch;
	}

	public String getBuildHost() {
		return _gitBuildHost;
	}

	public String getBuildTime() {
		return _gitBuildTime;
	}

	public String getBuildUserEmail() {
		return _gitBuildUserEmail;
	}

	public String getBuildUserName() {
		return _gitBuildUserName;
	}

	public String getBuildVersion() {
		return _gitBuildVersion;
	}

	public String getClosestTagCommitCount() {
		return _gitClosestTagCommitCount;
	}

	public String getClosestTagName() {
		return _gitClosestTagName;
	}

	public String getCommitIdAbbrev() {
		return _gitCommitIdAbbrev;
	}

	public String getCommitIdDescribeShort() {
		return _gitCommitIdDescribeShort;
	}

	public String getCommitIdDescribe() {
		return _gitCommitIdDescribe;
	}

	public String getCommitId() {
		return _gitCommitId;
	}

	public String getCommitMessageFull() {
		return _gitCommitMessageFull;
	}

	public String getCommitMessageShort() {
		return _gitCommitMessageShort;
	}

	public String getCommitTime() {
		return _gitCommitTime;
	}

	public String getCommitUserEmail() {
		return _gitCommitUserEmail;
	}

	public String getCommitUserName() {
		return _gitCommitUserName;
	}

	public String getDirty() {
		return _gitDirty;
	}

	public String getRemoteOriginUrl() {
		return _gitRemoteOriginUrl;
	}

	public String getTags() {
		return _gitTags;
	}

}
