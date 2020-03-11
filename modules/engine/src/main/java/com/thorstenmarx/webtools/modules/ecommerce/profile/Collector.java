/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thorstenmarx.webtools.modules.ecommerce.profile;

import com.thorstenmarx.webtools.api.analytics.query.ShardDocument;

/**
 *
 * @author marx
 */
public interface Collector {

	public void handle(final ShardDocument shardDocument);
}
