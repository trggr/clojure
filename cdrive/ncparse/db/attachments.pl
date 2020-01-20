#    attachments.pl - Extract "Agreement" attachments from SEC EDGAR filings.
#    Copyright (C)2011 openthinking <dwmcqueen@gmail.com>
#
#    This program is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Affero General Public License as
#    published by the Free Software Foundation, either version 3 of the
#    License, or (at your option) any later version.
#
#    This program is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Affero General Public License for more details.
#
#    You should have received a copy of the GNU Affero General Public License
#    along with this program.  If not, see <http://www.gnu.org/licenses/>.

# minimum version probably not required, but I only tested on 5.10
use 5.10.0;

# read the entire file
while(<>) { $content .= $_ }

# scan through each match of the regex
while ($content =~ m!<DOCUMENT>[\r\n]+
		             <TYPE>([^\r\n]*)[\r\n]+
		             <SEQUENCE>[^\r\n]*[\r\n]+
		             <FILENAME>([^\r\n]*)[\r\n]+
		             <DESCRIPTION>([^\r\n]*)[\r\n]+
		             <TEXT>(.*?)</TEXT>[\r\n]+</DOCUMENT>!sgx) {
		
	my ($filename, $description, $text) = ($2, $3, $4);
	
	# we are only interested in attachments with certain names
        #	if($description =~ /agreement/i) {
        if (1) {
		print "Saving: $filename\t$description\n";
		open(OUT, ">$filename") || die $!;
		
		# if a decoding step is every required, it should be added in this spot here.
		print OUT $text;
		
		close OUT || die $!;
	} else {
		print "Ignored: $filename\t$description\n";
	}
}