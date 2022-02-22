package com.teamcity.poolmovement;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.configuration.FileWatcher;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.serverSide.impl.FileWatcherFactory;
import jetbrains.buildServer.util.Dates;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.buildServer.util.StringUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.Boolean.parseBoolean;

public class BuildQueueStateManager {

    @NotNull
    private static final Logger LOG = Logger.getInstance(BuildQueueStateManager.class.getName());

    private static final String FILENAME = "plugin.queue-pauser.xml";

    interface FIELDS {
        String QUEUE_ENABLED = "queue-enabled";
        String CHANGED_ON = "state-changed-on";
    }

    private static final Map<String, String> DEFAULTS;

    static {
        final Map<String, String> defaults = new HashMap<>();
        // queue is enabled by default
        defaults.put(FIELDS.QUEUE_ENABLED, Boolean.toString(Boolean.TRUE));
        defaults.put(FIELDS.CHANGED_ON, Long.toString(Dates.makeDate(2012, 12, 12).getTime())); // 12.12.2012 0:0:0  for ease of testing
        DEFAULTS = Collections.unmodifiableMap(defaults);
    }

    private final AtomicReference<BuildQueueState> myStateRef = new AtomicReference<>(from(DEFAULTS));

    private final File myConfigFile;

    public BuildQueueStateManager(@NotNull final FileWatcherFactory fileWatcherFactory,
                             @NotNull final ServerPaths serverPaths) {
        myConfigFile = new File(serverPaths.getConfigDir(), FILENAME);
        FileWatcher myChangeObserver = fileWatcherFactory.createFileWatcher(myConfigFile);
        myChangeObserver.registerListener(it -> doLoad());
        myChangeObserver.start();
        doLoad();
    }

    @NotNull
    public BuildQueueState readQueueState() {
        return myStateRef.get();
    }

    private void doLoad() {
        if (!myConfigFile.exists() || !myConfigFile.canRead()) {
            return; // initial state is already loaded
        }
        Map<String, String> result = new HashMap<>();
        Element element;
        try {
            element = FileUtil.parseDocument(myConfigFile);
        } catch (Exception e) {
            LOG.warnAndDebugDetails("Failed to load usage statistics settings from file \"" + myConfigFile.getAbsolutePath() + "\"", e);
            return;
        }
        element.getChildren("param").forEach(it -> {
            if (it instanceof Element) {
                Element el = (Element) it;
                final String name = el.getAttributeValue("name");
                final String value = el.getAttributeValue("value");
                if (!StringUtil.isEmptyOrSpaces(name) && !StringUtil.isEmptyOrSpaces(value)) {
                    result.put(name, value);
                }
            }
        });
        myStateRef.set(from(result));
    }

    private BuildQueueState from(@NotNull final Map<String, String> properties) {
        return new BuildQueueState(
                parseBoolean(readValueWithDefault(properties, FIELDS.QUEUE_ENABLED)),
                new Date(Long.parseLong(readValueWithDefault(properties, FIELDS.CHANGED_ON)))
        );
    }

    private static String readValueWithDefault(@NotNull final Map<String, String> properties, @NotNull final String key) {
        return properties.getOrDefault(key, DEFAULTS.getOrDefault(key, ""));
    }
}