/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.type.descriptor.sql;

import java.io.Serializable;

import org.hibernate.type.descriptor.ValueBinder;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.java.BasicJavaDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Descriptor for the <tt>SQL</tt>/<tt>JDBC</tt> side of a value mapping.
 * <p/>
 * NOTE : Implementations should be registered with the {@link SqlTypeDescriptor}.  The built-in Hibernate
 * implementations register themselves on construction.
 *
 * @author Steve Ebersole
 */
public interface SqlTypeDescriptor extends Serializable {
	/**
	 * Return the {@linkplain java.sql.Types JDBC type-code} for the column mapped by this type.
	 *
	 * @apiNote Prefer {@link #getJdbcTypeCode}
	 *
	 * @return typeCode The JDBC type-code
	 */
	int getSqlType();

	/**
	 * Get the JDBC type code associated with this SQL type descriptor
	 *
	 * @see #getSqlType
	 */
	default int getJdbcTypeCode() {
		return getSqlType();
	}

	/**
	 * Is this descriptor available for remapping?
	 *
	 * @return {@code true} indicates this descriptor can be remapped; otherwise, {@code false}
	 *
	 * @see org.hibernate.type.descriptor.WrapperOptions#remapSqlTypeDescriptor
	 * @see org.hibernate.dialect.Dialect#remapSqlTypeDescriptor
	 */
	boolean canBeRemapped();

	@SuppressWarnings("unchecked")
	default <T> BasicJavaDescriptor<T> getJdbcRecommendedJavaTypeMapping(TypeConfiguration typeConfiguration) {
		// match legacy behavior
		return (BasicJavaDescriptor<T>) typeConfiguration.getJavaTypeDescriptorRegistry().getDescriptor(
				JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode( getSqlType() )
		);
	}

	/**
	 * todo (6.0) : move to {@link org.hibernate.metamodel.mapping.JdbcMapping}?
	 */
	default <T> JdbcLiteralFormatter<T> getJdbcLiteralFormatter(JavaTypeDescriptor<T> javaTypeDescriptor) {
		return (value, dialect, session) -> value.toString();
	}

	/**
	 * Get the binder (setting JDBC in-going parameter values) capable of handling values of the type described by the
	 * passed descriptor.
	 *
	 * @param javaTypeDescriptor The descriptor describing the types of Java values to be bound
	 *
	 * @return The appropriate binder.
	 */
	<X> ValueBinder<X> getBinder(JavaTypeDescriptor<X> javaTypeDescriptor);

	/**
	 * Get the extractor (pulling out-going values from JDBC objects) capable of handling values of the type described
	 * by the passed descriptor.
	 *
	 * @param javaTypeDescriptor The descriptor describing the types of Java values to be extracted
	 *
	 * @return The appropriate extractor
	 */
	<X> ValueExtractor<X> getExtractor(JavaTypeDescriptor<X> javaTypeDescriptor);
}
