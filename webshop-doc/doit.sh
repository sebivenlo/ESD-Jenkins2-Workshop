#!/bin/bash
mkdir -p out
(
for i in 1 2; do
pdflatex -recorder -jobname=doc_en -output-directory out <<EOF > /dev/null
\documentclass[dvipsnames,10pt,oneside,dvipsnames]{article}
\usepackage[utf8]{inputenc}
\providecommand\StudentId{PRO2/SEN1 Combined Assessment~\ExamDate}
\providecommand\StickNr{EN}
\input assessmentdoc_en
EOF
done
)&

wait
echo latex compiled 1 documents to dir ./out
echo resulting in out/*.pdf
