/** 
 * Copyright (C) 2018 European Spallation Source ERIC.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.phoebus.service.saveandrestore.persistence.dao.impl;

import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.AlarmStatus;
import org.phoebus.applications.saveandrestore.model.ConfigPv;
import org.phoebus.applications.saveandrestore.model.SnapshotItem;
import org.phoebus.service.saveandrestore.model.internal.SnapshotPv;
import org.phoebus.service.saveandrestore.persistence.dao.SnapshotDataConverter;
import org.phoebus.service.saveandrestore.persistence.dao.SnapshotPvDataType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SnapshotItemRowMapper implements RowMapper<SnapshotItem> {
	
	
	@Override
	public SnapshotItem mapRow(ResultSet resultSet, int rowIndex) throws SQLException {
		
		ConfigPv configPv = new ConfigPvRowMapper().mapRow(resultSet, rowIndex);
		
		SnapshotPv pvValue = SnapshotPv.builder()
				.configPv(configPv)
				.snapshotId(resultSet.getInt("snapshot_node_id"))
				.alarmSeverity(resultSet.getString("severity") == null ? null : AlarmSeverity.valueOf(resultSet.getString("severity")))
				.alarmStatus(resultSet.getString("status") ==  null ? null : AlarmStatus.valueOf(resultSet.getString("status")))
				.time(resultSet.getLong("time"))
				.timens(resultSet.getInt("timens"))
				.value(resultSet.getString("value"))
				.sizes(resultSet.getString("sizes"))
				.dataType(resultSet.getString("data_type") == null ? null : SnapshotPvDataType.valueOf(resultSet.getString("data_type")))
				.build();

		SnapshotPv readbackPvValue = null;
		String readbackValue = resultSet.getString("readback_value");
		if(readbackValue != null){
			readbackPvValue = SnapshotPv.builder()
					.alarmSeverity(resultSet.getString("readback_severity") == null ? null : AlarmSeverity.valueOf(resultSet.getString("readback_severity")))
					.alarmStatus(resultSet.getString("readback_status") ==  null ? null : AlarmStatus.valueOf(resultSet.getString("readback_status")))
					.time(resultSet.getLong("readback_time"))
					.timens(resultSet.getInt("readback_timens"))
					.value(readbackValue)
					.sizes(resultSet.getString("readback_sizes"))
					.dataType(resultSet.getString("readback_data_type") == null ? null : SnapshotPvDataType.valueOf(resultSet.getString("readback_data_type")))
					.build();
		}

		return SnapshotDataConverter.fromSnapshotPv(pvValue, readbackPvValue);
	}
}
