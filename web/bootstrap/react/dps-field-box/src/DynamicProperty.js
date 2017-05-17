import React, { Component } from 'react';

class DynamicProperty extends Component {


  render() {
    const json = this.props.value;
    const value = this.props.value.value;
    const id = json.name + "Field";
    const changeHandler = this.changeHandler;
    const _this = this;

    const controls = {
      checkBox: {
        normal: function() {
          return ( React.DOM.input({id: id, key: id, type: "checkbox", checked: value, onChange: changeHandler}));
        },
        readOnly: function() {
          return ( React.DOM.input({id: id, key: id, type: "checkbox", disabled: true, checked: value}) );
        }
      },
      comboBox: {
        normal: () => {
          var options = json.options.map(function(option) {
            return ( React.DOM.option({key: option.value, value: option.value}, option.text) );
          });
          if(json.canBeNull){
            options.unshift(  ( React.DOM.option({key: "", value: ""}, "all") ) );
          }
          return (
            React.DOM.select({id: id, ref: 'editableComboBox', key: id, defaultValue: value, onChange: changeHandler, className: this.props.controlClassName},
              options
            )
          );
        },
        readOnly: () => {
          const selectedOption = json.options.filter(option => option.value === json.value);
          const text = selectedOption.length ? selectedOption[0].text : json.value;
          return this.createStatic(text);
        }
      },
      textArea: {
        normal: () => (
          <textarea placeholder={json.tips.placeholder} id={id} key={id} rows={json.rows} cols={json.columns} value={value} onChange={changeHandler} className={this.props.controlClassName}/>
        ),
        readOnly: () => this.createStatic(value)
      },
      textInput: {
        normal: <input type="text" className="form-control" placeholder={json.tips.placeholder} id={id} key={id} value={value} />,
        readOnly: this.createStatic(value)
      },
      passwordInput: {
        normal: <input type="password" placeholder={json.tips.placeholder} id={id} key={id} value={value}
                       onChange={changeHandler} className={this.props.controlClassName}/>,
        readOnly: this.createStatic('******')
      }
    };

    const renderer = controls[json.type] || controls['textInput'];
    const valueControl = renderer[json.isReadOnly ? 'readOnly' : 'normal'];
    const label = <label htmlFor={id} className={this.props.labelClassName}>{json.title}</label>;
    const helpTextElement = json.tips.helpText ? <span className={this.props.helpTextClassName!=null?this.props.helpTextClassName:"help-block"}>{json.tips.helpText}</span> : undefined;
    const hasDanger = !json.error && value === '' ? 'error' : '';

    return (
      <div className={(this.props.className!=null?this.props.className:'form-group dynamic-property') + ' ' + hasDanger}>
        {label}
        <div className="controls">
          {valueControl}
          {helpTextElement}
        </div>
      </div>
    );
  }

  createStatic(value) {
    return <p className="form-control-static" dangerouslySetInnerHTML={{__html: value}}></p>;
  }

}

export default DynamicProperty;
