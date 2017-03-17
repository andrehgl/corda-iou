package com.iou.kotlin.contract

import com.iou.kotlin.state.IOUState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.TransactionForContract
import net.corda.core.contracts.requireThat
import net.corda.core.crypto.SecureHash

class IOUContract : Contract {
    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: TransactionForContract) { return }

    /** This is a reference to the underlying legal contract template and associated parameters.  */
    override val legalContractReference = SecureHash.sha256("IOU contract template and params")
}