import React, { ReactNode, forwardRef } from 'react';
import { ShieldAlert } from 'lucide-react';

interface ReportLayoutProps {
    title: string;
    subtitle?: string;
    children: ReactNode;
}

export const ReportLayout = forwardRef<HTMLDivElement, ReportLayoutProps>(
    ({ title, subtitle, children }, ref) => {
        const currentDate = new Date().toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        return (
            <div ref={ref} className="bg-white w-[210mm] min-h-[297mm] p-12 mx-auto relative shadow-sm print:shadow-none print:w-full print:h-full print:p-0">

                {/* HEADER */}
                <div className="flex justify-between items-start border-b-2 border-slate-900 pb-6 mb-8">
                    <div>
                        <div className="flex items-center gap-2 text-indigo-700 mb-2">
                            <ShieldAlert className="w-8 h-8" />
                            <span className="text-xl font-bold tracking-tight">SIMCCS System</span>
                        </div>
                        <h1 className="text-3xl font-black text-slate-900 tracking-tight uppercase">{title}</h1>
                        {subtitle && <h2 className="text-lg text-slate-500 font-medium mt-1">{subtitle}</h2>}
                    </div>

                    <div className="text-right flex flex-col items-end">
                        <div className="bg-slate-100 text-slate-700 px-3 py-1 rounded font-mono text-sm mb-2 font-semibold">
                            OFFICIAL RECORD
                        </div>
                        <div className="text-sm text-slate-500 font-medium">Generated On:</div>
                        <div className="text-sm text-slate-900 font-mono">{currentDate}</div>
                    </div>
                </div>

                {/* CONTENT (Flex-grow to push footer down if needed, but in print usually absolute footer is safer) */}
                <div className="mb-24">
                    {children}
                </div>

                {/* FOOTER */}
                <div className="absolute bottom-12 left-12 right-12 border-t border-slate-200 pt-4 flex justify-between items-center text-xs text-slate-400 font-mono">
                    <div>Crisis Communication & Management System</div>
                    <div>Page <span className="pageNumber"></span></div>
                    <div>Confidential & Proprietary</div>
                </div>

            </div>
        );
    });
