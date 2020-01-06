/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.type.descriptor.sql.internal;

import java.time.temporal.TemporalAccessor;
import javax.persistence.TemporalType;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.spi.BasicJdbcLiteralFormatter;

/**
 * @author Steve Ebersole
 */
public class JdbcLiteralFormatterTemporal extends BasicJdbcLiteralFormatter {
	private final TemporalType precision;

	public JdbcLiteralFormatterTemporal(JavaTypeDescriptor javaTypeDescriptor, TemporalType precision) {
		super( javaTypeDescriptor );
		this.precision = precision;
	}

	@Override
	public String toJdbcLiteral(Object value, Dialect dialect, SharedSessionContractImplementor session) {
		// for performance reasons, avoid conversions if we can
		if ( value instanceof java.util.Date ) {
			return dialect.formatDateTimeLiteral(
					(java.util.Date) value,
					precision
			);
		}
		else if ( value instanceof java.util.Calendar ) {
			return dialect.formatDateTimeLiteral(
					(java.util.Calendar) value,
					precision
			);
		}
		else if ( value instanceof TemporalAccessor ) {
			return dialect.formatDateTimeLiteral(
					(TemporalAccessor) value,
					precision
			);
		}

		switch ( precision) {
			case DATE: {
				return dialect.formatDateTimeLiteral(
						unwrap( value, java.sql.Date.class, session ),
						precision
				);
			}
			case TIME: {
				return dialect.formatDateTimeLiteral(
						unwrap( value, java.sql.Time.class, session ),
						precision
				);
			}
			default: {
				return dialect.formatDateTimeLiteral(
						unwrap( value, java.util.Date.class, session ),
						precision
				);
			}
		}
	}
}
