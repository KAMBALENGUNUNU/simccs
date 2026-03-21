import React from 'react';
import { RiskAuditDTO } from '../../types/api';
import { ReportLayout } from './ReportLayout';
import { AlertOctagon } from 'lucide-react';

interface RiskAuditPDFProps {
    data: RiskAuditDTO;
}

export const RiskAuditPDF = React.forwardRef<HTMLDivElement, RiskAuditPDFProps>(
    ({ data }, ref) => {
        return (
            <div ref={ref}>
                <ReportLayout
                    title="Misinformation & Risk Audit"
                    subtitle="Detailed Log of Flagged Reports"
                >

                    <div className="flex gap-8 mb-8 bg-slate-50 p-6 border border-slate-200 rounded-lg">
                        <div>
                            <div className="text-sm text-slate-500 uppercase font-bold tracking-wider">Total Flags Raised</div>
                            <div className="text-4xl font-black text-rose-600">{data.totalFlags}</div>
                        </div>
                        <div>
                            <div className="text-sm text-slate-500 uppercase font-bold tracking-wider">Unique Reports Affected</div>
                            <div className="text-4xl font-black text-slate-800">{data.uniqueReportsFlagged}</div>
                        </div>
                    </div>

                    <div className="space-y-6">
                        {data.flaggedReports.map((report, idx) => (
                            <div key={idx} className="border border-slate-200 rounded-lg p-6 bg-white shadow-sm page-break-inside-avoid">
                                <div className="flex items-start justify-between mb-4">
                                    <div className="flex items-start gap-3">
                                        <AlertOctagon className="w-6 h-6 text-rose-500 mt-1 shrink-0" />
                                        <div>
                                            <h4 className="text-lg font-bold text-slate-900 leading-tight">
                                                Report #{report.reportId}: {report.reportTitle}
                                            </h4>
                                            <p className="text-sm text-slate-500 mt-1">Author: <span className="font-semibold text-slate-700">{report.authorName}</span></p>
                                        </div>
                                    </div>
                                    <div className="text-right text-xs font-mono text-slate-400">
                                        {new Date(report.flaggedAt).toLocaleString()}
                                    </div>
                                </div>

                                <div className="bg-rose-50 border border-rose-100 rounded p-4 text-sm text-slate-800">
                                    <div className="font-bold text-rose-800 mb-1 flex justify-between">
                                        <span>Flag Context</span>
                                        <span className="text-xs uppercase bg-white px-2 py-0.5 rounded border border-rose-200">Flagged by: {report.flaggedBy}</span>
                                    </div>
                                    <p className="whitespace-pre-wrap">{report.reason}</p>
                                </div>
                            </div>
                        ))}

                        {data.flaggedReports.length === 0 && (
                            <div className="text-center py-12 text-slate-400 border-2 border-dashed border-slate-200 rounded-lg">
                                No misinformation flags recorded in the current period.
                            </div>
                        )}
                    </div>

                </ReportLayout>
            </div>
        );
    }
);

RiskAuditPDF.displayName = 'RiskAuditPDF';
