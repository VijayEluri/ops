package com.serwylo.ops.electrodes.phases

import com.serwylo.ops.DumbProfiler
import com.serwylo.ops.Phase
import com.serwylo.uno.spreadsheet.Calculator
import com.sun.star.table.XCellRange

/**
 *
 */
class FindWeirdElectrodes extends ElectrodesPhase {

	Double lowerThreshold = -30
	Double upperThreshold = 50

	@Override
	boolean requiresUserInteraction() {
		false
	}

	@Override
	String getName() {
		"Find suspect electrodes"
	}

	@Override
	String getDescription() {
		"Suspect electrodes have values that are far above or below the usual value." +
		"These are not guaranteed to be stuffy, but we will ask you to confirm when we've found them anyway."
	}

	@Override
	void execute() {

		Calculator calc        = new Calculator( model.document, 0 )
		XCellRange valueRange  = model.document[ 0 ].getCellRangeByPosition( 0, model.headers.startRow, model.headers.electrodeLabels.size() - 1, 1000000 )
		List<Double> maxValues = calc.maxForEachColumn( valueRange )
		List<Double> minValues = calc.minForEachColumn( valueRange )

		model.headers.electrodeLabels.each {

			String label = it.key
			int colIndex = it.value

			if ( model.deadColumns.contains( colIndex ) ) {
				return
			}

			double max = maxValues[ colIndex ]
			double min = minValues[ colIndex ]
			if ( max > upperThreshold ) {
				println "Max value for electrode $label: $max - thats weird."
				model.weirdColumns.add( label )
			} else if ( min < lowerThreshold ) {
				println "Min value for electrode $label: $min - thats weird"
				model.weirdColumns.add( label )
			}
		}

	}
	
}
