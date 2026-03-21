import React from 'react';
import { PersonnelStatsDTO } from '../../types/api';
import { ReportLayout } from './ReportLayout';

interface PersonnelPDFProps {
    data: PersonnelStatsDTO;
}

export const PersonnelPDF = React.forwardRef<HTMLDivElement, PersonnelPDFProps>(
    ({ data }, ref) => {
        return (
            <div ref={ref}>
                <ReportLayout
                    title="Personnel Engagement"
                    subtitle="Activity Audit for Journalists and Editors"
                >

                    {/* Journalists Section */}
                    <div className="mb-12">
                        <h3 className="text-xl font-bold text-slate-800 border-b border-slate-300 pb-2 mb-4">
                            Journalist Performance
                        </h3>

                        <table className="w-full text-sm text-left text-slate-600">
                            <thead className="text-xs text-slate-700 uppercase bg-slate-50 border-b border-slate-300">
                                <tr>
                                    <th scope="col" className="px-4 py-3">Name</th>
                                    <th scope="col" className="px-4 py-3 text-center">Total Submitted</th>
                                    <th scope="col" className="px-4 py-3 text-center">Verified</th>
                                    <th scope="col" className="px-4 py-3 text-center">Rejected</th>
                                    <th scope="col" className="px-4 py-3 text-right">Accuracy Rate</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.journalists.map((j) => (
                                    <tr key={j.id} className="bg-white border-b border-slate-100 font-mono">
                                        <td className="px-4 py-3 font-medium text-slate-900 whitespace-nowrap overflow-hidden text-ellipsis max-w-[200px]" title={j.email}>
                                            <div>{j.name}</div>
                                            <div className="text-xs text-slate-400 font-sans">{j.email}</div>
                                        </td>
                                        <td className="px-4 py-3 text-center text-slate-500 font-bold">{j.totalReports}</td>
                                        <td className="px-4 py-3 text-center text-emerald-600 font-bold">{j.verifiedReports}</td>
                                        <td className="px-4 py-3 text-center text-rose-500">{j.rejectedReports}</td>
                                        <td className="px-4 py-3 text-right">
                                            <span className={`px-2 py-1 rounded font-bold ${j.accuracyRate > 80 ? 'bg-emerald-100 text-emerald-800' : j.accuracyRate > 50 ? 'bg-amber-100 text-amber-800' : 'bg-rose-100 text-rose-800'}`}>
                                                {j.accuracyRate.toFixed(1)}%
                                            </span>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {/* Editors Section */}
                    <div>
                        <h3 className="text-xl font-bold text-slate-800 border-b border-slate-300 pb-2 mb-4">
                            Editor Activity
                        </h3>

                        <table className="w-full text-sm text-left text-slate-600">
                            <thead className="text-xs text-slate-700 uppercase bg-slate-50 border-b border-slate-300">
                                <tr>
                                    <th scope="col" className="px-4 py-3">Name</th>
                                    <th scope="col" className="px-4 py-3 text-center">Total Reviews</th>
                                    <th scope="col" className="px-4 py-3 text-center">Misinformation Flags Raised</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data.editors.map((e) => (
                                    <tr key={e.id} className="bg-white border-b border-slate-100 font-mono">
                                        <td className="px-4 py-3 font-medium text-slate-900 whitespace-nowrap overflow-hidden text-ellipsis max-w-[200px]" title={e.email}>
                                            <div>{e.name}</div>
                                            <div className="text-xs text-slate-400 font-sans">{e.email}</div>
                                        </td>
                                        <td className="px-4 py-3 text-center text-indigo-600 font-bold">{e.totalReviews}</td>
                                        <td className="px-4 py-3 text-center text-amber-600 font-bold">{e.flagsRaised}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                </ReportLayout>
            </div>
        );
    }
);

PersonnelPDF.displayName = 'PersonnelPDF';
