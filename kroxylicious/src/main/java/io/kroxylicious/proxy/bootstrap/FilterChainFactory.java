/*
 * Copyright Kroxylicious Authors.
 *
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.kroxylicious.proxy.bootstrap;

import io.kroxylicious.proxy.config.Configuration;
import io.kroxylicious.proxy.filter.KrpcFilter;
import io.kroxylicious.proxy.internal.filter.FilterContributorManager;
import io.kroxylicious.proxy.service.ClusterEndpointConfigProvider;

/**
 * Abstracts the creation of a chain of filter instances, hiding the configuration
 * required for instantiation at the point at which instances are created.
 * New instances are created during initialization of a downstream channel.
 */
public class FilterChainFactory {

    private final Configuration config;
    private final ClusterEndpointConfigProvider clusterEndpointConfigProvider;

    public FilterChainFactory(Configuration config, ClusterEndpointConfigProvider clusterEndpointConfigProvider) {
        this.config = config;
        this.clusterEndpointConfigProvider = clusterEndpointConfigProvider;
    }

    /**
     * Create a new chain of filter instances
     * @return the new chain.
     */
    public KrpcFilter[] createFilters() {
        FilterContributorManager filterContributorManager = FilterContributorManager.getInstance();

        return config.filters()
                .stream()
                .map(f -> filterContributorManager.getFilter(f.type(), clusterEndpointConfigProvider, f.config()))
                .toArray(KrpcFilter[]::new);
    }
}
