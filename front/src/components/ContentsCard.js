import React from 'react';

class ContentsCard extends React.Component {
	render() {
		return (
			<div className="panel">
				<div className="head">
					<strong>Panel Heading {this.props.count}</strong>
				</div>
				<div className="body">
					Panel Body {this.props.count}
				</div>
			</div>
		);
	}
}

export default ContentsCard;