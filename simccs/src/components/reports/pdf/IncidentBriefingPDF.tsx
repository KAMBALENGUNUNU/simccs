import React from 'react';
import { ReportResponse } from '../../types/api';
import { ReportLayout } from './ReportLayout';
import { MapPin, Users, Calendar } from 'lucide-react';

interface IncidentBriefingPDFProps {
    data: ReportResponse;
}

export const IncidentBriefingPDF = React.forwardRef<HTMLDivElement, IncidentBriefingPDFProps>(
    ({ data }, ref) => {
        return (
            <div ref={ref}>
                <ReportLayout
                    title="Crisis Incident Briefing"
                    subtitle={`Report Reference: INT-REP-${data.id.toString().padStart(6, '0')}`}
                >

                    {/* Metadata Bar */}
                    <div className="flex flex-wrap gap-4 p-4 bg-slate-50 border border-slate-200 rounded-lg mb-8">
                        <div className="flex items-center gap-2 pr-4 border-r border-slate-300">
                            <Calendar className="w-4 h-4 text-slate-400" />
                            <div className="text-sm font-semibold text-slate-700">
                                {new Date(data.createdAt).toLocaleString()}
                            </div>
                        </div>

                        <div className="flex items-center gap-2 pr-4 border-r border-slate-300">
                            <MapPin className="w-4 h-4 text-rose-500" />
                            <div className="text-sm">
                                <span className="font-semibold text-slate-700">{data.locationName || 'Unspecified Location'} </span>
                                <span className="text-slate-500 font-mono text-xs">({data.latitude.toFixed(4)}, {data.longitude.toFixed(4)})</span>
                            </div>
                        </div>

                        <div className="flex items-center gap-2">
                            <Users className="w-4 h-4 text-amber-500" />
                            <div className="text-sm font-semibold text-slate-700">
                                {data.casualtyCount || 0} Estimated Casualties
                            </div>
                        </div>
                    </div>

                    {/* Primary Content */}
                    <div className="grid grid-cols-3 gap-8 mb-8">
                        <div className="col-span-2">
                            <h3 className="text-2xl font-bold tracking-tight text-slate-900 mb-2">{data.title}</h3>

                            <div className="mb-6 flex gap-2">
                                {data.categories?.map(cat => (
                                    <span key={cat} className="px-2 py-1 bg-indigo-50 text-indigo-700 text-xs font-bold rounded">
                                        {cat.toUpperCase()}
                                    </span>
                                ))}
                                <span className={`px-2 py-1 text-xs font-bold rounded uppercase ${data.status === 'PUBLISHED' ? 'bg-emerald-100 text-emerald-800' : 'bg-slate-200 text-slate-700'}`}>
                                    Status: {data.status}
                                </span>
                                {data.flagged && (
                                    <span className="px-2 py-1 bg-rose-100 text-rose-800 text-xs font-bold rounded uppercase">
                                        Misinformation Risk
                                    </span>
                                )}
                            </div>

                            <div className="prose prose-sm max-w-none text-slate-700 font-serif leading-relaxed space-y-4">
                                <div>
                                    <h4 className="font-sans font-bold text-slate-900 text-sm uppercase tracking-wider mb-2">Executive Summary</h4>
                                    <p className="bg-slate-50 p-4 rounded border-l-4 border-slate-900 font-medium whitespace-pre-wrap">{data.summary}</p>
                                </div>

                                <div>
                                    <h4 className="font-sans font-bold text-slate-900 text-sm uppercase tracking-wider mt-6 mb-2">Detailed Report Content</h4>
                                    <p className="whitespace-pre-wrap">{data.content}</p>
                                </div>
                            </div>
                        </div>

                        {/* Sidebar Details */}
                        <div className="col-span-1 border-l border-slate-200 pl-8">
                            <h4 className="text-sm font-bold text-slate-900 uppercase tracking-wider mb-4 border-b border-slate-200 pb-2">Chain of Custody</h4>

                            <div className="space-y-4 text-sm">
                                <div>
                                    <div className="text-slate-500 text-xs font-semibold mb-1">Submitting Journalist</div>
                                    <div className="font-mono bg-slate-100 px-2 py-1 rounded text-slate-800 inline-block">
                                        {data.authorName}
                                    </div>
                                </div>

                                <div>
                                    <div className="text-slate-500 text-xs font-semibold mb-1">Creation Timestamp</div>
                                    <div className="font-mono text-slate-700">
                                        {new Date(data.createdAt).toISOString()}
                                    </div>
                                </div>

                                <div>
                                    <div className="text-slate-500 text-xs font-semibold mb-1">System Internal ID</div>
                                    <div className="font-mono text-slate-700">
                                        {data.id}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                </ReportLayout>
            </div>
        );
    }
);

IncidentBriefingPDF.displayName = 'IncidentBriefingPDF';
