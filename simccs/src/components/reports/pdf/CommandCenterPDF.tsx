import React from 'react';
import { DashboardStats } from '../../types/api';
import { ReportLayout } from './ReportLayout';
import { Activity, CheckCircle, ShieldAlert, FileText } from 'lucide-react';

interface CommandCenterPDFProps {
    data: DashboardStats;
}

export const CommandCenterPDF = React.forwardRef<HTMLDivElement, CommandCenterPDFProps>(
    ({ data }, ref) => {
        return (
            <div ref={ref}>
                <ReportLayout
                    title="Command Center Overview"
                    subtitle="Global Crisis Operations Summary"
                >

                    {/* Key Metrics Grid */}
                    <div className="grid grid-cols-4 gap-4 mb-10">
                        <div className="bg-slate-50 border border-slate-200 p-4 rounded-lg">
                            <div className="flex items-center gap-2 text-slate-500 mb-2">
                                <FileText className="w-4 h-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Total Active Reports</span>
                            </div>
                            <div className="text-3xl font-black text-slate-800">{data.totalReports}</div>
                        </div>

                        <div className="bg-amber-50 border border-amber-200 p-4 rounded-lg">
                            <div className="flex items-center gap-2 text-amber-700 mb-2">
                                <Activity className="w-4 h-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Awaiting Review</span>
                            </div>
                            <div className="text-3xl font-black text-amber-600">{data.pendingReports}</div>
                        </div>

                        <div className="bg-emerald-50 border border-emerald-200 p-4 rounded-lg">
                            <div className="flex items-center gap-2 text-emerald-700 mb-2">
                                <CheckCircle className="w-4 h-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Verified & Published</span>
                            </div>
                            <div className="text-3xl font-black text-emerald-600">{data.approvedReports}</div>
                        </div>

                        <div className="bg-rose-50 border border-rose-200 p-4 rounded-lg">
                            <div className="flex items-center gap-2 text-rose-700 mb-2">
                                <ShieldAlert className="w-4 h-4" />
                                <span className="text-xs font-bold uppercase tracking-wider">Misinfo Risks</span>
                            </div>
                            <div className="text-3xl font-black text-rose-600">{data.flaggedReports}</div>
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-8 mb-8">
                        {/* Status Breakdown Table */}
                        <div>
                            <h3 className="text-lg font-bold text-slate-800 border-b border-slate-300 pb-2 mb-4">
                                Raw Status Breakdown
                            </h3>
                            <table className="w-full text-sm text-left">
                                <tbody>
                                    {Object.entries(data.reportsByStatus).map(([status, count]) => (
                                        <tr key={status} className="border-b border-slate-100 last:border-0">
                                            <td className="py-2 font-mono text-slate-600">{status}</td>
                                            <td className="py-2 text-right font-bold text-slate-900">{count}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>

                        {/* Recent Activity Table */}
                        <div>
                            <h3 className="text-lg font-bold text-slate-800 border-b border-slate-300 pb-2 mb-4">
                                Recent Incident Logs
                            </h3>
                            <table className="w-full text-sm text-left">
                                <tbody>
                                    {data.recentActivity.map((activity) => (
                                        <tr key={activity.id} className="border-b border-slate-100 last:border-0">
                                            <td className="py-2 max-w-[150px] truncate text-slate-800 font-medium pr-2" title={activity.title}>
                                                {activity.title}
                                            </td>
                                            <td className="py-2 text-xs font-bold text-slate-500 uppercase">{activity.status}</td>
                                            <td className="py-2 text-right text-xs font-mono text-slate-400">
                                                {new Date(activity.createdAt).toLocaleDateString()}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* System Impact Notice */}
                    <div className="bg-indigo-50 border border-indigo-100 p-6 rounded-lg text-indigo-900">
                        <h4 className="font-bold mb-2">System Impact Assessment</h4>
                        <p className="text-sm opacity-90">
                            The platform is currently tracking <strong className="font-black text-indigo-700">{data.totalCasualties}</strong> estimated casualties across all active incident reports.
                            Review priority should be given to pending reports in high-impact regions.
                        </p>
                    </div>

                </ReportLayout>
            </div>
        );
    }
);

CommandCenterPDF.displayName = 'CommandCenterPDF';
